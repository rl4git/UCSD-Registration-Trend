const colorSelector = new ColorSelector();
const alreadyFetchedCoursesDepartmentId = [];
const courseCache = {};

class CourseTab {
    constructor() {
        // store the courses from fetch
        this.fetchedCourses = [];
        this.courseLineColors = [];
        // store the overall department - id course information
        this.OverallCourse = {};
        this.currentCourse = "WhatEver";

        // Create the main course-tab div
        this.tabElement = document.createElement("div");
        this.tabElement.className = "course-tab";
        this.tabElement.innerHTML = `
            <div class="unit-container">
                <div class="input-section">
                    <label for="department">Department:</label>
                    <input type="text" class="department-input" placeholder="e.g., CSE" maxlength="6">

                    <label for="course-id">ID:</label>
                    <input type="text" class="course-id-input" placeholder="e.g., 151A">
                </div>

                <div class="options-section">
                    <label class="toggle-label small-label" data-tooltip="Displays data for all courses below.">Overall Display</label>
                    <label class="toggle-switch">
                        <input type="checkbox" class="overall-display">
                        <span class="slider small-slider"></span>
                    </label>

                    <label class="toggle-label small-label" data-tooltip="One-click hides all associated courses (excluding Overall Display).">Hide</label>
                    <label class="toggle-switch">
                        <input type="checkbox" class="hide">
                        <span class="slider small-slider"></span>
                    </label>

                    <!-- Color picker for tabElement -->
                    <input type="color" class="tab-color-picker" title="Select Color" />
                    
                    <button class="btn large-btn check-btn">Check</button>
                    <button class="btn large-btn remove-btn">Remove</button>
                </div>
            </div>
            <div class="course-professor-tabs"></div>
        `;

        // Initialize the tab color picker with a random color from colorSelector
        const tabColorPicker = this.tabElement.querySelector(".tab-color-picker");
        tabColorPicker.value = colorSelector.selectColor();
        this.courseLineColors.push(tabColorPicker.value); // Track the selected color

        // Listener to update color and ensure it's set as selected
        tabColorPicker.addEventListener("input", (event) => {
            const color = event.target.value;
            updateLineColor(this.OverallCourse, color); // Update color in chart or display
        });

        // Set up listeners for check, remove, and hidden toggles
        this.tabElement.querySelector(".check-btn").onclick = () => this.handleCheck();
        this.tabElement.querySelector(".remove-btn").onclick = () => this.removeTab();
        this.tabElement.querySelector(".hide").onchange = () => this.toggleHidden();
        this.tabElement.querySelector(".overall-display").onchange = () => this.overallDisplay();
    }

    handleCheck() {
        let department = this.tabElement.querySelector(".department-input").value.trim();
        let courseId = this.tabElement.querySelector(".course-id-input").value.trim();
        department = department.toUpperCase();
        courseId = courseId.toUpperCase();
    
        if (alreadyFetchedCoursesDepartmentId.includes(`${department}-${courseId}`)) {
            alert("The course you entered already been queried.");
            return;
        }
    
        this.fetchedCourses.forEach(courseComposeIds => removeCourseLine(courseComposeIds));
        const index = alreadyFetchedCoursesDepartmentId.indexOf(this.currentCourse);
        if (index !== -1) {
            alreadyFetchedCoursesDepartmentId.splice(index, 1);
        }
        this.returnAllColors();
    
        this.currentCourse = `${department}-${courseId}`;
        alreadyFetchedCoursesDepartmentId.push(this.currentCourse);
        this.fetchedCourses = [];
        this.OverallCourse = {};
        this.returnAllColors();
    
        if (!department || !courseId) {
            alert("Please select a department and enter a course ID.");
            return;
        }
    
        const url = `https://ucsdregistration.com/courses/${encodeURIComponent(department)}%20${encodeURIComponent(courseId)}`;
        // const url = `http://localhost:7000/courses/${encodeURIComponent(department)}%20${encodeURIComponent(courseId)}`;
        
        const cacheKey = `${department}-${courseId}`;
    
        // 使用 courseCache 替代 localStorage
        const cachedData = courseCache[cacheKey];
        if (cachedData) {
            if(cachedData.length === 0){
                this.showNotFoundMessage();
            } else {
                this.renderCourses(cachedData);
                cachedData.forEach(course =>
                    this.fetchedCourses.push(`${course.courseId} - ${course.professorFirstName} ${course.professorLastName} - ${course.academicQuarter}`)
                );
                this.setOverallCourses(department, courseId, cachedData);
                this.fetchedCourses.push(`${this.OverallCourse.courseId} - ${this.OverallCourse.professorFirstName} ${this.OverallCourse.professorLastName} - ${this.OverallCourse.academicQuarter}`);
            }
            this.afterCheckButtonResetOverallAndHidden();
            return;
        }
    
        fetch(url)
            .then(response => {
                if (!response.ok) throw new Error("Network response was not ok");
                return response.json();
            })
            .then(data => {
                courseCache[cacheKey] = data;
                if (data.length === 0) {
                    this.showNotFoundMessage();
                } else {
                    this.renderCourses(data);
                    data.forEach(course =>
                        this.fetchedCourses.push(`${course.courseId} - ${course.professorFirstName} ${course.professorLastName} - ${course.academicQuarter}`)
                    );
                    this.setOverallCourses(department, courseId, data);
                    this.fetchedCourses.push(`${this.OverallCourse.courseId} - ${this.OverallCourse.professorFirstName} ${this.OverallCourse.professorLastName} - ${this.OverallCourse.academicQuarter}`);
                }
            })
            .then(() => {
                this.afterCheckButtonResetOverallAndHidden();
            })
            .catch(error => showNotification("Error: " + error.message));
    }    

    afterCheckButtonResetOverallAndHidden() {
        const overallToggle = this.tabElement.querySelector(".overall-display");
        if (overallToggle.checked) {
            overallToggle.checked = false;
            this.overallDisplay();
        }
        const hideToggle = this.tabElement.querySelector(".hide");
        if (hideToggle.checked) {
            hideToggle.checked = false;
            this.toggleHidden();
        }
    }

    removeTab() {
        // Remove lines specific to this tab
        this.fetchedCourses.forEach(courseComposeIds => removeCourseLine(courseComposeIds));
        // Remove the current course tab course from the global course recorder
        const index = alreadyFetchedCoursesDepartmentId.indexOf(this.currentCourse);
        if (index !== -1) {
            alreadyFetchedCoursesDepartmentId.splice(index, 1); // 从索引位置移除一个元素
        }
        // return all colors so they can be used for other lines
        this.returnAllColors();
        this.tabElement.remove();
    }

    overallDisplay() {
        const overallToggle = this.tabElement.querySelector(".overall-display");
        const hideToggle = this.tabElement.querySelector(".hide");
    
        if (overallToggle.checked) {
            // Show OverallCourse if overallToggle is checked
            if (!hideToggle.checked) {
                hideToggle.checked = true;
                this.toggleHidden(); // Ensure other elements are shown
            }
            addCourseLine(this.OverallCourse, this.tabElement.querySelector(".tab-color-picker").value);
        } else {
            // Hide OverallCourse if overallToggle is unchecked
            if (hideToggle.checked) {
                hideToggle.checked = false;
                this.toggleHidden(); // Ensure other elements are shown
            }
            removeCourseLine(`${this.OverallCourse.courseId} - ${this.OverallCourse.professorFirstName} ${this.OverallCourse.professorLastName} - ${this.OverallCourse.academicQuarter}`);
        }
    }

    toggleHidden() {
        const hideToggle = this.tabElement.querySelector(".hide");
        const professorTabsContainer = this.tabElement.querySelector(".course-professor-tabs");

        if (hideToggle.checked) {
            // Hide the course-professor-tabs if any hidden-btn is inactive
            const professorTabs = professorTabsContainer.querySelectorAll(".course-professor-tab");

            professorTabs.forEach(tab => {
                const hiddenBtn = tab.querySelector(".line-toggle");
                if (hiddenBtn.checked) {
                    hiddenBtn.click(); // Simulate click to hide the line if not hidden
                }
            });

            professorTabsContainer.style.display = "none"; // Hide the entire container
        } else {
            // Show the course-professor-tabs and re-enable each line
            professorTabsContainer.style.display = ""; // Reset display property

            const professorTabs = professorTabsContainer.querySelectorAll(".course-professor-tab");

            professorTabs.forEach(tab => {
                const hiddenBtn = tab.querySelector(".line-toggle");
                if (!hiddenBtn.checked) {
                    hiddenBtn.click(); // Simulate click to show the line if it was hidden
                }
            });
        }
    }

    // Method to show the "not found" message
    showNotFoundMessage() {
        const tabsContainer = this.tabElement.querySelector(".course-professor-tabs");
        tabsContainer.innerHTML = ""; // Clear previous content
    
        const notFoundTab = document.createElement("div");
        notFoundTab.className = "course-professor-tab";
        notFoundTab.style.color = "gray"; // Set text color to red
        notFoundTab.textContent = "Sorry, the course you are looking for does not exist in our dataset.";
    
        tabsContainer.appendChild(notFoundTab);
    }

    renderCourses(courses) {
        const tabsContainer = this.tabElement.querySelector(".course-professor-tabs");
        tabsContainer.innerHTML = ""; // Clear previous content

        courses.forEach(course => {
            const courseTab = document.createElement("div");
            courseTab.className = "course-professor-tab";

            const courseInfo = document.createElement("div");
            courseInfo.className = "course-info";
            courseInfo.innerHTML = `
                <span class="professor-name">${course.professorFirstName} ${course.professorMiddleName || ""} ${course.professorLastName}</span> - 
                ${course.courseId} - ${course.academicQuarter} - Size: ${course.courseSize}`;


            const colorPicker = document.createElement("input");
            colorPicker.type = "color";
            colorPicker.className = "color-picker";
            colorPicker.value = colorSelector.selectColor();    // pick the random color
            this.courseLineColors.push(colorPicker.value);      // remember the color for return when we remove the tab
            colorPicker.addEventListener("input", (event) => {
                const color = event.target.value;
                updateLineColor(course, color);
            });

            const toggleSwitch = document.createElement("label");
            toggleSwitch.className = "toggle-switch";

            const toggleInput = document.createElement("input");
            toggleInput.type = "checkbox";
            toggleInput.checked = true;
            toggleInput.className = "line-toggle";
            toggleInput.addEventListener("change", () => toggleLineVisibility(course, toggleInput, courseTab, colorPicker.value));

            const slider = document.createElement("span");
            slider.className = "slider small-slider";

            toggleSwitch.appendChild(toggleInput);
            toggleSwitch.appendChild(slider);

            courseTab.appendChild(courseInfo);
            courseTab.appendChild(colorPicker);
            courseTab.appendChild(toggleSwitch);
            tabsContainer.appendChild(courseTab);

            addCourseLine(course, colorPicker.value);
        });
    }

    setOverallCourses(department, courseId, courses){
        this.OverallCourse = {
            courseId: `${department} ${courseId}`,
            academicQuarter: "Overall",
            courseSize: 0,
            availableSpots: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            enrolledStudents: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            waitlistCount: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            professorFirstName: "None",
            professorMiddleName: null,
            professorLastName: ""
        };
        courses.forEach(course =>{
            this.OverallCourse.courseSize += course.courseSize;
            this.OverallCourse.availableSpots = this.OverallCourse.availableSpots.map((value, index) => value + course.availableSpots[index]);
            this.OverallCourse.enrolledStudents = this.OverallCourse.enrolledStudents.map((value, index) => value + course.enrolledStudents[index]);
            this.OverallCourse.waitlistCount = this.OverallCourse.waitlistCount.map((value, index) => value + course.waitlistCount[index]);
        });
    }

    returnAllColors(){
        this.courseLineColors.forEach(color =>{
            colorSelector.returnColor(color);
        });
        this.courseLineColors = [];
    }

    appendToContainer() {
        document.getElementById("course-tabs-container").appendChild(this.tabElement);
    }
}