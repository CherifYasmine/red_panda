import { useState, useEffect } from 'react';
import { THEME } from '../../constants/theme';
import client from '../../api/client';
import { getErrorMessage } from '../../types/BackendError';
import type { Course } from '../../types/Course';
import type { Teacher } from '../../types/Teacher';
import type { Classroom } from '../../types/Classroom';

interface CreateCourseSectionForm {
  courseId: number | null;
  teacherId: number | null;
  classroomId: number | null;
  capacity: number | null;
}

interface CreateSectionProps {
  onSectionCreated?: () => void;
  courseId?: number;
}

export function CreateSection({ onSectionCreated, courseId }: CreateSectionProps) {
  const [courses, setCourses] = useState<Course[]>([]);
  const [teachers, setTeachers] = useState<Teacher[]>([]);
  const [classrooms, setClassrooms] = useState<Classroom[]>([]);

  const [filteredTeachers, setFilteredTeachers] = useState<Teacher[]>([]);
  const [filteredClassrooms, setFilteredClassrooms] = useState<Classroom[]>([]);

  const [form, setForm] = useState<CreateCourseSectionForm>({
    courseId: null,
    teacherId: null,
    classroomId: null,
    capacity: null,
  });

  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const loadData = async () => {
      try {
        setIsLoading(true);
        setError(null);

        const [coursesRes, teachersRes, classroomsRes] = await Promise.all([
          client.get<{ content: Course[] }>('/courses?page=0&size=100'),
          client.get<Teacher[]>('/teachers'),
          client.get<Classroom[]>('/classrooms'),
        ]);

        setCourses(coursesRes.data.content || []);
        setTeachers(teachersRes.data || []);
        setClassrooms(classroomsRes.data || []);

        // If courseId is provided, pre-select it
        if (courseId) {
          const course = (coursesRes.data.content || []).find((c) => c.id === courseId);
          if (course) {
            setForm({
              courseId: course.id,
              teacherId: null,
              classroomId: null,
              capacity: null,
            });

            const courseSpecializationId = course.specialization?.id;
            if (courseSpecializationId) {
              const filtered = (teachersRes.data || []).filter(
                (t) => t.specialization?.id === courseSpecializationId
              );
              setFilteredTeachers(filtered);
            }

            const roomTypeId = course.specialization?.roomType?.id;
            if (roomTypeId) {
              const filtered = (classroomsRes.data || []).filter(
                (c) => c.roomType?.id === roomTypeId
              );
              setFilteredClassrooms(filtered);
            }
          }
        }
      } catch (err) {
        setError(getErrorMessage(err, 'Failed to load data'));
      } finally {
        setIsLoading(false);
      }
    };

    loadData();
  }, [courseId]);

  const handleCourseChange = (courseId: string) => {
    const selectedCourseId = parseInt(courseId);
    setForm({
      courseId: selectedCourseId,
      teacherId: null,
      classroomId: null,
      capacity: null,
    });

    if (!selectedCourseId) {
      setFilteredTeachers([]);
      setFilteredClassrooms([]);
      return;
    }

    const course = courses.find((c) => c.id === selectedCourseId);
    if (!course) return;

    const courseSpecializationId = course.specialization?.id;
    if (courseSpecializationId) {
      const filtered = teachers.filter(
        (t) => t.specialization?.id === courseSpecializationId
      );
      setFilteredTeachers(filtered);
    }

    const roomTypeId = course.specialization?.roomType?.id;
    if (roomTypeId) {
      const filtered = classrooms.filter(
        (c) => c.roomType?.id === roomTypeId
      );
      setFilteredClassrooms(filtered);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccessMessage(null);

    if (!form.courseId || !form.teacherId || !form.classroomId || !form.capacity) {
      setError('Please fill in all fields');
      return;
    }

    if (form.capacity < 1 || form.capacity > 100) {
      setError('Capacity must be between 1 and 100');
      return;
    }

    try {
      setIsSubmitting(true);

      const payload = {
        courseId: form.courseId,
        teacherId: form.teacherId,
        classroomId: form.classroomId,
        capacity: form.capacity,
      };

      await client.post('/course-sections', payload);
      setSuccessMessage('Course section created successfully!');

      setForm({
        courseId: null,
        teacherId: null,
        classroomId: null,
        capacity: null,
      });
      setFilteredTeachers([]);
      setFilteredClassrooms([]);

      if (onSectionCreated) {
        setTimeout(onSectionCreated, 500);
      }

      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to create course section'));
    } finally {
      setIsSubmitting(false);
    }
  };

  const selectedCourse = form.courseId
    ? courses.find((c) => c.id === form.courseId)
    : null;

  if (isLoading) {
    return (
      <div className="flex justify-center items-center py-12">
        <div className="animate-spin">
          <svg className="w-8 h-8 text-cyan-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.5a8.5 8.5 0 1 1-2.5 5" />
          </svg>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {error && (
        <div className="mb-6 p-4 bg-red-50 border-l-4 border-red-500 rounded flex items-start justify-between">
          <p className="text-red-700 font-semibold flex-1">{error}</p>
          <button onClick={() => setError(null)} className="ml-4 text-red-500 hover:text-red-700 font-bold">
            ✕
          </button>
        </div>
      )}

      {successMessage && (
        <div className="mb-6 p-4 bg-green-50 border-l-4 border-green-500 rounded flex items-start justify-between">
          <p className="text-green-700 font-semibold flex-1">{successMessage}</p>
          <button onClick={() => setSuccessMessage(null)} className="ml-4 text-green-500 hover:text-green-700 font-bold">
            ✕
          </button>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        {!courseId && (
          <div>
            <label className={`block text-sm font-semibold ${THEME.colors.text.primary} mb-2`}>
              Course *
            </label>
            <select
              value={form.courseId || ''}
              onChange={(e) => handleCourseChange(e.target.value)}
              className={`w-full px-4 py-2 border-2 ${THEME.colors.borders.light} rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500`}
              required
            >
              <option value="">-- Select a course --</option>
              {courses.map((course) => (
                <option key={course.id} value={course.id}>
                  {course.code} - {course.name}
                </option>
              ))}
            </select>
          </div>
        )}

        {selectedCourse && (
          <div className={`p-4 rounded-lg border-2 ${THEME.colors.borders.light} bg-blue-50`}>
            <p className={`text-sm ${THEME.colors.text.primary} mb-2`}>
              <span className="font-semibold">Specialization:</span> {selectedCourse.specialization?.name}
            </p>
            <p className={`text-sm ${THEME.colors.text.primary}`}>
              <span className="font-semibold">Room Type Required:</span> {selectedCourse.specialization?.roomType?.name}
            </p>
          </div>
        )}

        <div>
          <label className={`block text-sm font-semibold ${THEME.colors.text.primary} mb-2`}>
            Teacher *
          </label>
          <select
            value={form.teacherId || ''}
            onChange={(e) => setForm((prev) => ({ ...prev, teacherId: e.target.value ? parseInt(e.target.value) : null }))}
            className={`w-full px-4 py-2 border-2 ${THEME.colors.borders.light} rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500`}
            disabled={filteredTeachers.length === 0}
            required
          >
            <option value="">
              {filteredTeachers.length === 0 ? '-- Select a course first --' : '-- Select a teacher --'}
            </option>
            {filteredTeachers.map((teacher) => (
              <option key={teacher.id} value={teacher.id}>
                {teacher.firstName} {teacher.lastName}
              </option>
            ))}
          </select>
          {form.courseId && filteredTeachers.length === 0 && (
            <p className="text-red-600 text-sm mt-2">⚠️ No teachers found for this course's specialization</p>
          )}
        </div>

        <div>
          <label className={`block text-sm font-semibold ${THEME.colors.text.primary} mb-2`}>
            Classroom *
          </label>
          <select
            value={form.classroomId || ''}
            onChange={(e) => setForm((prev) => ({ ...prev, classroomId: e.target.value ? parseInt(e.target.value) : null }))}
            className={`w-full px-4 py-2 border-2 ${THEME.colors.borders.light} rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500`}
            disabled={filteredClassrooms.length === 0}
            required
          >
            <option value="">
              {filteredClassrooms.length === 0 ? '-- Select a course first --' : '-- Select a classroom --'}
            </option>
            {filteredClassrooms.map((classroom) => (
              <option key={classroom.id} value={classroom.id}>
                {classroom.name} ({classroom.roomType?.name})
              </option>
            ))}
          </select>
          {form.courseId && filteredClassrooms.length === 0 && (
            <p className="text-red-600 text-sm mt-2">⚠️ No classrooms found with the required room type</p>
          )}
        </div>

        <div>
          <label className={`block text-sm font-semibold ${THEME.colors.text.primary} mb-2`}>
            Capacity (1-10) *
          </label>
          <input
            type="number"
            min="1"
            max="10"
            value={form.capacity || ''}
            onChange={(e) => setForm((prev) => ({ ...prev, capacity: e.target.value ? parseInt(e.target.value) : null }))}
            className={`w-full px-4 py-2 border-2 ${THEME.colors.borders.light} rounded-lg focus:outline-none focus:ring-2 focus:ring-cyan-500`}
            placeholder="Enter section capacity"
            required
          />
        </div>

        <button
          type="submit"
          disabled={
            isSubmitting ||
            !form.courseId ||
            !form.teacherId ||
            !form.classroomId ||
            !form.capacity
          }
          className={`w-full px-6 py-3 font-semibold rounded-lg transition-all ${
            isSubmitting ||
            !form.courseId ||
            !form.teacherId ||
            !form.classroomId ||
            !form.capacity
              ? 'bg-slate-200 text-slate-400 cursor-not-allowed'
              : `bg-gradient-to-r ${THEME.colors.gradients.button} text-white hover:shadow-md`
          }`}
        >
          {isSubmitting ? 'Creating...' : 'Create Course Section'}
        </button>
      </form>
    </div>
  );
}
