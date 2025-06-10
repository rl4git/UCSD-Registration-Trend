// Add Course button function to create and append a new CourseTab
function addCourseTab() {
    const newCourseTab = new CourseTab();
    newCourseTab.appendToContainer();
}

// Notification for errors
function showNotification(message) {
    alert(message);
}

// switch charts button
function toggleChartDisplay() {
    const ratioChartContainer = document.getElementById("ratio-chart");
    const numericChartContainer = document.getElementById("numeric-chart");
    const toggleButton = document.getElementById("ratio-display-btn");

    if (ratioChartContainer.style.display === "none") {
        // Show ratio chart, hide numeric chart
        ratioChartContainer.style.display = "block";
        numericChartContainer.style.display = "none";
        toggleButton.textContent = "Switch To Numeric Charts";
    } else {
        // Show numeric chart, hide ratio chart
        ratioChartContainer.style.display = "none";
        numericChartContainer.style.display = "block";
        toggleButton.textContent = "Switch To Ratio Chart";
    }
}
