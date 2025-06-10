// Initialize the Chart
let lineChart = null;

function initializeLineChart() {
    const ctx = document.querySelector(".line-chart").getContext("2d");
    lineChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: [
                'Prior', 'First Pass Priorities & Seniors', 'First Pass Juniors', 
                'First Pass Sophomores', 'First Pass First-Year', 
                'Second Pass Priorities & Seniors', 'Second Pass Juniors', 
                'Second Pass Sophomores', 'Second Pass First-Year', 
                'One Day After', 'Quarter Start'
            ],
            datasets: [
                // Invisible line at y=0 with ID 'example'
                {
                    label: 'No Available Line',
                    data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                    borderColor: 'rgba(0, 0, 0, 0)', // Fully transparent
                    borderWidth: 0,
                    id: 'example',
                    hidden: false // Keep hidden
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    title: {
                        display: true,
                        text: 'Available Spots / Course Size',
                        font: {
                            weight: 'bold',
                            size: 14 // Increase slightly for visibility
                        }
                    },
                    grid: {
                        drawBorder: false,
                        color: function(context) {
                            if (context.tick.value === 0) {
                                return '#000'; // Black color for y=0 line
                            }
                            return Chart.defaults.borderColor;
                        },
                        lineWidth: function(context) {
                            if (context.tick.value === 0) {
                                return 2; // Thicker line for y=0
                            }
                            return 1;
                        },
                        borderDash: function(context) {
                            if (context.tick.value === 0) {
                                return [5, 5]; // Dashed line for y=0
                            }
                            return [];
                        }
                    }
                }
            },
            plugins: {
                ooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.dataset.label || '';
                            const value = context.raw !== null ? context.raw.toFixed(2) : 'N/A';
                            return `${label}: ${value}`;
                        }
                    }
                }
            }
        }
    });
}

// Call initializeLineChart once on page load
initializeLineChart();

function addCourseLine(course, color) {
    // Calculate base data: availableSpots - waitlistCount
    const availableSpotsRatio = course.availableSpots.map((spot, index) => 
        (spot - course.waitlistCount[index]) / course.courseSize
    );
    const courseId = `${course.courseId} - ${course.professorFirstName} ${course.professorLastName} - ${course.academicQuarter}`;
    const courseLabel = `${course.courseId} - ${course.professorFirstName} ${course.professorLastName} - ${course.academicQuarter}`;

    // Call numeric chart addition with adjusted data
    addCourseNumericLine(course, color);

    // Add dataset to lineChart (ratio chart)
    if (!lineChart.data.datasets.find(ds => ds.id === courseId)) {
        lineChart.data.datasets.push({
            label: courseLabel,
            data: availableSpotsRatio,
            borderColor: color,
            backgroundColor: color,
            fill: false,
            hidden: false,
            id: courseId
        });
        lineChart.update();
    }
}

function updateLineColor(course, color) {

    // 调用 numeric line chart
    updateNumericLineColor(course, color)

    const courseId = `${course.courseId} - ${course.professorFirstName} ${course.professorLastName} - ${course.academicQuarter}`;
    const dataset = lineChart.data.datasets.find(ds => ds.id === courseId);
    if (dataset) {
        dataset.borderColor = color;
        dataset.backgroundColor = color;
        lineChart.update();
    }
}

function toggleLineVisibility(course, toggleInput, courseTab, color) {

    // 调用 numeric line chart
    toggleNumericLineVisibility(course, toggleInput, courseTab, color)

    const courseId = `${course.courseId} - ${course.professorFirstName} ${course.professorLastName} - ${course.academicQuarter}`;
    const datasetIndex = lineChart.data.datasets.findIndex(ds => ds.id === courseId);

    if (datasetIndex !== -1) {
        lineChart.data.datasets.splice(datasetIndex, 1);
        toggleInput.checked = false;
        courseTab.style.backgroundColor = "#e0e0e0";
    } else {
        this.addCourseLine(course, color);
        toggleInput.checked = true;
        courseTab.style.backgroundColor = "#ffffff";
    }

    lineChart.update();
}

function removeCourseLine(courseComposeId) {

    // 调用 numeric line chart
    removeNumericCourseLine(courseComposeId);

    const datasetIndex = lineChart.data.datasets.findIndex(ds => ds.id === courseComposeId);

    if (datasetIndex !== -1) {
        // Remove the dataset from the chart
        lineChart.data.datasets.splice(datasetIndex, 1);
        lineChart.update();
    } else {
        console.warn(`No dataset found with ID: ${courseComposeId}`);
    }
}