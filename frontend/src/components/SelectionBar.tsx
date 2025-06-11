'use client';

import { useState, useEffect } from 'react';
import courseCatalog from '../../../mock_data/course_catalog.json';

export default function SelectionBar() {
  const [selectedDepartment, setSelectedDepartment] = useState<string>('CSE');
  const [selectedCourse, setSelectedCourse] = useState<string>('');
  const [availableCourses, setAvailableCourses] = useState<string[]>([]);

  useEffect(() => {
    // Update available courses when department changes
    setAvailableCourses(courseCatalog[selectedDepartment as keyof typeof courseCatalog] || []);
    setSelectedCourse(courseCatalog[selectedDepartment as keyof typeof courseCatalog]?.[0] || '');
  }, [selectedDepartment]);

  return (
    <div className="w-full max-w-[1000px] h-auto min-h-[120px] bg-[#F2F2F2] rounded-[30px] p-6 mx-auto">
      <div className="flex flex-col md:flex-row items-center justify-between gap-4 max-w-[900px] mx-auto">
        <div className="space-y-2 w-full md:w-auto">
          <label className="block text-xl font-bold text-black">🏫 DEPARTMENT</label>
          <div className="relative">
            <select
              value={selectedDepartment}
              onChange={(e) => setSelectedDepartment(e.target.value)}
              className="w-full md:w-[200px] h-[45px] bg-white rounded-[20px] shadow-md px-4 text-[#414141] text-base font-medium appearance-none cursor-pointer"
            >
              {Object.keys(courseCatalog).map((dept) => (
                <option key={dept} value={dept}>
                  {dept}
                </option>
              ))}
            </select>
            <div className="absolute right-3 top-1/2 -translate-y-1/2 w-[20px] h-[20px] bg-[#D9D9D9] rounded-full flex items-center justify-center pointer-events-none">
              <div className="w-0 h-0 border-l-[5px] border-l-transparent border-r-[5px] border-r-transparent border-t-[6px] border-t-white pointer-events-none" />
            </div>
          </div>
        </div>

        <div className="space-y-2 w-full md:w-auto">
          <label className="block text-xl font-bold text-black">📚 COURSE ID</label>
          <div className="relative">
            <select
              value={selectedCourse}
              onChange={(e) => setSelectedCourse(e.target.value)}
              className="w-full md:w-[160px] h-[45px] bg-white rounded-[20px] shadow-md px-4 text-[#414141] text-base font-medium appearance-none cursor-pointer"
            >
              {availableCourses.map((course) => (
                <option key={course} value={course}>
                  {course}
                </option>
              ))}
            </select>
            <div className="absolute right-3 top-1/2 -translate-y-1/2 w-[20px] h-[20px] bg-[#D9D9D9] rounded-full flex items-center justify-center pointer-events-none">
              <div className="w-0 h-0 border-l-[5px] border-l-transparent border-r-[5px] border-r-transparent border-t-[6px] border-t-white pointer-events-none" />
            </div>
          </div>
        </div>

        <button className="w-full md:w-[180px] h-[45px] bg-[#005EB8] rounded-xl shadow-md text-white text-base font-bold hover:bg-[#004a8f] transition-colors">
          ADD TO CHART
        </button>
      </div>
    </div>
  );
}
