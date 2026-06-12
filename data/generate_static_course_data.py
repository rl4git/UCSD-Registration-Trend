import csv
import json
from collections import defaultdict
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
FINAL_TABLE_DIR = ROOT / "data" / "cleaned_data" / "ucsd" / "final_table"
OUTPUT_DIR = ROOT / "old_frontend" / "public" / "data" / "courses"
QUARTER_ORDER = {
    "Winter": 1,
    "Spring": 2,
    "Summer": 3,
    "Fall": 4,
}


def course_file_key(department, course_id):
    raw_key = f"{department}_{course_id}".upper().strip()
    safe_chars = []
    previous_was_separator = False

    for char in raw_key:
        if char.isascii() and char.isalnum():
            safe_chars.append(char)
            previous_was_separator = False
        elif not previous_was_separator:
            safe_chars.append("_")
            previous_was_separator = True

    return "".join(safe_chars).strip("_")


def read_csv_rows(table_name):
    table_dir = FINAL_TABLE_DIR / table_name
    for csv_path in sorted(table_dir.glob("*.csv")):
        with csv_path.open("r", encoding="utf-8", newline="") as csv_file:
            yield from csv.DictReader(csv_file)


def load_courses():
    courses = {}
    for row in read_csv_rows("courses"):
        course_offering_id = row["course_offering_id"]
        courses[course_offering_id] = {
            "course_offering_id": course_offering_id,
            "department": row["department"].upper(),
            "course_id": row["course_id"].upper(),
            "year": int(row["year"]),
            "quarter": row["quarter"],
            "total": int(row["total"]),
            "instructor": row["instructor"],
        }
    return courses


def load_snapshots():
    snapshots = {}
    for row in read_csv_rows("enrollment_snapshots"):
        key = (row["course_offering_id"], row["date"])
        snapshots[key] = {
            "waitlist": int(row["waitlist"]),
            "enrolled": int(row["enrolled_ct"]),
        }
    return snapshots


def load_passtimes():
    passtimes = defaultdict(list)
    seen = set()
    for row in read_csv_rows("passtimes"):
        key = (int(row["year"]), row["quarter"])
        dedupe_key = (*key, row["passtime"])
        if dedupe_key in seen:
            continue
        seen.add(dedupe_key)
        passtimes[key].append(row["passtime"])

    for key in passtimes:
        passtimes[key].sort()

    return passtimes


def build_course_chart(course, snapshots, passtimes):
    course_size = course["total"]
    dates = passtimes[(course["year"], course["quarter"])]

    available_spots = []
    enrolled_students = []
    waitlist_count = []

    for date in dates:
        snapshot = snapshots.get((course["course_offering_id"], date))
        if snapshot:
            enrolled = snapshot["enrolled"]
            waitlist = snapshot["waitlist"]
        else:
            enrolled = 0
            waitlist = 0

        enrolled_students.append(enrolled)
        waitlist_count.append(waitlist)
        available_spots.append(course_size - enrolled)

    return {
        "courseId": f"{course['department']} {course['course_id']}",
        "academicQuarter": f"{course['year']} {course['quarter']}",
        "courseSize": course_size,
        "professorFirstName": course["instructor"],
        "professorMiddleName": "",
        "professorLastName": "",
        "availableSpots": available_spots,
        "waitlistCount": waitlist_count,
        "enrolledStudents": enrolled_students,
    }


def write_course_files(courses, snapshots, passtimes):
    grouped_courses = defaultdict(list)
    for course in courses.values():
        key = (course["department"], course["course_id"])
        grouped_courses[key].append(course)

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    for existing_file in OUTPUT_DIR.glob("*.json"):
        existing_file.unlink()

    manifest = {}
    file_keys = {}
    for (department, course_id), course_group in sorted(grouped_courses.items()):
        file_key = course_file_key(department, course_id)
        file_name = f"{file_key}.json"
        course_key = f"{department} {course_id}"
        existing_course_key = file_keys.get(file_key)
        if existing_course_key and existing_course_key != course_key:
            raise ValueError(
                f"Course file key collision: {existing_course_key} and {course_key} both map to {file_name}"
            )
        file_keys[file_key] = course_key
        manifest[course_key] = file_name

        chart_rows = [
            build_course_chart(course, snapshots, passtimes)
            for course in sorted(
                course_group,
                key=lambda item: (
                    item["year"],
                    QUARTER_ORDER.get(item["quarter"], 99),
                    item["instructor"],
                ),
            )
        ]

        with (OUTPUT_DIR / file_name).open("w", encoding="utf-8") as json_file:
            json.dump(chart_rows, json_file, separators=(",", ":"))

    with (OUTPUT_DIR.parent / "course-manifest.json").open("w", encoding="utf-8") as json_file:
        json.dump(manifest, json_file, indent=2, sort_keys=True)

    return len(grouped_courses)


def main():
    courses = load_courses()
    snapshots = load_snapshots()
    passtimes = load_passtimes()
    course_file_count = write_course_files(courses, snapshots, passtimes)

    print(f"Loaded {len(courses)} unique course offerings.")
    print(f"Loaded {len(snapshots)} unique enrollment snapshots.")
    print(f"Generated {course_file_count} static course JSON files in {OUTPUT_DIR}.")


if __name__ == "__main__":
    main()
