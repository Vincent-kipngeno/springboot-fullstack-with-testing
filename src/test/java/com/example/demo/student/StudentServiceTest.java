package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    private StudentService underTest;

    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository);
    }

    @Test
    void canGetAllStudents() {
        //when
        underTest.getAllStudents();
        //then
        verify(studentRepository).findAll();
    }

    @Test
    void  canAddStudent() {
        //given
        String email = "jamilla@gmail.com";
        Student student = new Student(
                "Jamilla",
                email,
                Gender.FEMALE
        );

        //when
        underTest.addStudent(student);

        //then
        ArgumentCaptor<Student> studentArgumentCaptor =
                ArgumentCaptor.forClass(Student.class);

        verify(studentRepository)
                .save(studentArgumentCaptor.capture());

        Student capturedStudent = studentArgumentCaptor.getValue();

        assertThat(capturedStudent).isEqualTo(student);
    }

    @Test
    void  willThrowWhenEmailIsTaken() {
        //given
        String email = "jamilla@gmail.com";
        Student student = new Student(
                "Jamilla",
                email,
                Gender.FEMALE
        );

        /*
        given(studentRepository.selectExistsEmail(student.getEmail()))
                .willReturn(true);
        */
        given(studentRepository.selectExistsEmail(anyString()))
                .willReturn(true);

        //when
        //then
        assertThatThrownBy(() -> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        verify(studentRepository, never()).save(any());
    }

    @Test
    void canDeleteStudent() {
        //given
        Long studentId = 1L;

        given(studentRepository.existsById(anyLong()))
                .willReturn(true);

        //when
        underTest.deleteStudent(studentId);

        //then
        ArgumentCaptor<Long> studentIdArgumentCaptor =
                ArgumentCaptor.forClass(Long.class);

        verify(studentRepository)
                .deleteById(studentIdArgumentCaptor.capture());

        Long capturedStudentId = studentIdArgumentCaptor.getValue();

        assertThat(capturedStudentId).isEqualTo(studentId);
    }

    @Test
    void  willThrowWhenIdNotExists() {
        //given
        Long studentId = 1L;

        given(studentRepository.existsById(anyLong()))
                .willReturn(false);

        //then
        assertThatThrownBy(() -> underTest.deleteStudent(studentId))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + studentId + " does not exists")  ;

        verify(studentRepository, never()).deleteById(any());
    }
}