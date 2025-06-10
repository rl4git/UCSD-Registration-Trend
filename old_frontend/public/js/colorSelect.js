class ColorSelector {
    constructor() {
        this.colors = [
            "#ea5545", "#f46a9b", "#ef9b20", "#edbf33", "#ede15b", "#bdcf32", "#87bc45", "#27aeef", "#b33dc6",
            "#b30000", "#7c1158", "#4421af", "#1a53ff", "#0d88e6", "#00b7c7", "#5ad45a", "#8be04e",
            "#e60049", "#0bb4ff", "#50e991", "#e6d800", "#9b19f5", "#ffa300", "#dc0ab4", "#b3d4ff", "#00bfa0"
        ];
        this.availableColors = new Set(this.colors); // 用 Set 存储可用的颜色
    }

    // 随机选择一个颜色，标记为不可用
    selectColor() {
        // 如果有可用颜色，从 availableColors 中随机选择一个
        if (this.availableColors.size > 0) {
            const colorArray = Array.from(this.availableColors); // 将 Set 转换为数组
            const randomIndex = Math.floor(Math.random() * colorArray.length);
            const selectedColor = colorArray[randomIndex];
            this.availableColors.delete(selectedColor); // 标记颜色为不可用
            return selectedColor;
        }
        
        // 如果没有可用颜色，随机返回一个已使用的颜色
        const randomIndex = Math.floor(Math.random() * this.colors.length);
        return this.colors[randomIndex];
    }

    // 将指定的颜色设置为可用
    returnColor(color) {
        if (this.colors.includes(color)) { // 确保颜色在初始颜色列表中
            this.availableColors.add(color); // 将颜色添加回可用集合
        }
    }
}
