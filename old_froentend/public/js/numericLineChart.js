// numericLineChart.js
let numericChart = null;

function initializeNumericLineChart() {
    const ctx = document.querySelector(".numeric-chart").getContext("2d");
    numericChart = new Chart(ctx, {
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
                        text: 'Available Spots (Unit: Seats)',
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
                tooltip: {
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

// Call initializeNumericLineChart once on page load
initializeNumericLineChart();

function addCourseNumericLine(course, color) {
    // Calculate base data without dividing by course size
    const availableSpots = course.availableSpots.map((spot, index) => 
        spot - course.waitlistCount[index]
    );
    const courseId = `${course.courseId} - ${course.professorFirstName} ${course.professorLastName} - ${course.academicQuarter}`;
    const courseLabel = `${course.courseId} - ${course.professorFirstName} ${course.professorLastName} - ${course.academicQuarter}`;

    // Add dataset to numericChart
    if (!numericChart.data.datasets.find(ds => ds.id === courseId)) {
        numericChart.data.datasets.push({
            label: courseLabel,
            data: availableSpots,
            borderColor: color,
            backgroundColor: color,
            fill: false,
            hidden: false,
            id: courseId
        });
        numericChart.update();
    }
}

function updateNumericLineColor(course, color) {
    const courseId = `${course.courseId} - ${course.professorFirstName} ${course.professorLastName} - ${course.academicQuarter}`;
    const dataset = numericChart.data.datasets.find(ds => ds.id === courseId);
    if (dataset) {
        dataset.borderColor = color;
        dataset.backgroundColor = color;
        numericChart.update();
    }
}

function toggleNumericLineVisibility(course, toggleInput, courseTab, color) {
    const courseId = `${course.courseId} - ${course.professorFirstName} ${course.professorLastName} - ${course.academicQuarter}`;
    const datasetIndex = numericChart.data.datasets.findIndex(ds => ds.id === courseId);

    if (datasetIndex !== -1) {
        numericChart.data.datasets.splice(datasetIndex, 1);
        toggleInput.checked = false;
        courseTab.style.backgroundColor = "#e0e0e0";
    } else {
        this.addCourseNumericLine(course, color);
        toggleInput.checked = true;
        courseTab.style.backgroundColor = "#ffffff";
    }

    numericChart.update();
}

function removeNumericCourseLine(courseComposeId) {
    const datasetIndex = numericChart.data.datasets.findIndex(ds => ds.id === courseComposeId);

    if (datasetIndex !== -1) {
        // Remove the dataset from the chart
        numericChart.data.datasets.splice(datasetIndex, 1);
        numericChart.update();
    } else {
        console.warn(`No dataset found with ID: ${courseComposeId}`);
    }
}

function removeNumericCoursesLine(coursesIds){
    coursesIds.forEach(courseComposeId => {
        removeNumericCourseLine(courseComposeId);
    });
}
