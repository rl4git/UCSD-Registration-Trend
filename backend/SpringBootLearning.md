### 生成初始包，和简单构建 HelloWorld 网页

0. 在电脑上安装 JDK，我用的是 JDK 17。再安装 MAVEN。
1. 去 `https://start.spring.io/` 初始化一个 Spring Boot 应用包
   - Maven
   - Java 17
   - Spring Boot 3.5.3
   - 项目信息
   - JDK
   - Dependencies:
     - spring-boot-starter-web
     - spring-boot-devtools
     - spring-boot-starter-data-jpa
     - lombok
2. 生成，解压，就得到了 maven 结构的项目。

   - `pom.xml`: 整个项目的配置目录，包括项目信息，依赖，一些设置。
   - `src/main/java/...一堆项目文件目录.../`: 这是放代码文件的地方，开发主要在这里进行
   - `src/main/resources/`: 放公共资源的地方。比如前后端不分离的话，就可以阿布前端文件放这里。
   - `src/main/resources/application.properties`: Spring Boot 的配置文件，比如服务器端口，日志级别，数据库链接信息，都在这里。
   - `src/test/.../`: 放测试代码的地方
   - `target/`: 代码写完后，编译和打包的文件会放到这里。
     > 我用的是 VSCode，可以安装一些插件，很有用，比如 `Spring Boot Extension Pack`，可以在创建文件时自动生成一些初始代码。

3. 关于 mvn 和 mvnw

   - mvn 就是 maven，这是我们管理项目结构和依赖的工具。
   - mvnw 是 maven warper，Spring Boot 自带的一个 mvn 的包装工具

     ```bash
     # 常用maven指令, 现在用mvnw

     mvnw clean    # 删除target目录，清楚所有旧的编译和打包文件，建议在重新打包前执行
     mvnw compile  # 只编译项目源代码（src/main/java 下的文件），生成 .class 文件到 target/classes 目录。
     mvnw test     # 编译并运行所有单元测试（src/test/java 下的文件）。
     mvnw spring-boot:run  # 在开发环境中启动应用
     mvnw package  # 非常重要。它会执行 compile, test 等一系列步骤，并最终将你的项目打包成一个可执行的 JAR 文件，存放在 target 目录下。
     mvnw install  # 比 package 多做一步：在打包后，它还会将生成的 JAR 文件安装到你本地的 Maven 仓库 (通常在你的用户主目录下的 .m2 文件夹里）。如果你的项目需要被其他本地项目依赖，这个命令就很有用。
     mvnw dependency:tree  # 有用的排错工具。它会以树状结构打印出项目的所有依赖，包括间接依赖。当你遇到“依赖冲突”（比如两个库引入了不同版本的同一个 JAR 包）时，用这个命令可以一目了然地看到问题出在哪里。
     ```

4. 写一个简单的 hellow world 网页

   - 进入`src/main/java/...一堆项目文件目录.../`
   - `*Application.java` 是项目（网页）的启动入口。
     - `@SpringBootApplication()` 标记了它是一个 Spring Boot 应用
     - 如果依赖里包含了数据库相关，但是还没链接数据库，可以修改为 `@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })` 暂时移除数据库
   - 创建目录`Controller/`，用来接受 http 请求
   - 创建文件`Controller/HelloWorld.java`，你应该能看懂下面的代码。

     ```java
      package com.ucsdregistration.web_backend.Controller;
      import org.springframework.web.bind.annotation.GetMapping;
      import org.springframework.web.bind.annotation.RestController;
      import org.springframework.web.bind.annotation.RequestParam;

      @RestController
      public class HelloWorldController {

        @GetMapping("/hello")
        public String sayHello() {
            return "Hello World!";
        }
      }
     ```

   - 在主目录下运行 `./mvnw spring-boot:run` 运行网站
   - 默认端口为 8080，打开浏览器访问 `localhost:8080/hello`，应该能看见 `Hello World!`

### 部署到 EC2 并设置 Nginx 和 Cloudflare

1. 在本地开发环境运行 `./mvnw clean package`，将项目打包为 target/\*.jar 文件
2. 将这个 jar 文件上传到 EC2，不管是手动上传，SCP，还是 git 都行
3. 在 EC2，运行 `nohup java -jar 你的jar文件的路径.jar --server.port=8080 > app.log 2>&1 &`，这会让网站泡在后台，端口为 8080，日志输出到当前目录的 app.log

   - 如何停止后台网站进程：

     ```bash
     # 找到进程ID
     ps -ef | grep web-backend
     # 输出类似于 ubuntu   12345   1  0 00:00 ? 00:00:05 java -jar web-backend-0.0.1-SNAPSHOT.jar ...
     # 其中 12345 就是pid

     kill 12345 # 杀死进程
     ```

4. `curl localhost:8080/hello` 观察网站是否在运行。
5. 接下来配置 nginx，本来可以直接监听所有 80 端口的请求，转发到本地的 8080 请求，然而，**由于我们之前已经配置了一个 nginx 监听 8080 的 ucsdregistration.com**请求，直接配置一个新的 nginx 监听所有 80 请求会造成冲突。因此，我决定在 cloduflare 添加一条新的 `api.ucsdregistration.com` 记录，专门用于项目测试。
6. 创建 Nginx 配置文件 `sudo vim /etc/nginx/sites-available/springboot-hello`

   ```bash
   server {
       listen 80;
       server_name api.ucsdregistration.com; # 转发所有 api.ucsdregistration.com 请求

       location / {
           proxy_pass http://localhost:8080; # 转发到 Spring Boot 应用的地址
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
       }
   }
   ```

7. 启用配置，并重启 Nginx

   ```bash
    # 启用配置
    sudo ln -s /etc/nginx/sites-available/springboot-hello /etc/nginx/sites-enabled/

    # 测试配置文件是否有语法错误
    sudo nginx -t
    # 如果显示 "syntax is ok" 和 "test is successful"，则可以重启
    sudo systemctl restart nginx
   ```

8. 配置 Cloudlfare

- 添加一条新的 DNS 记录：
  - 类型：A
  - 名称：api（只需要填写 api）
  - 值：EC2 的公网 IP

9. 等待生效，然后访问 `api.ucsdregistration.com/hello`
