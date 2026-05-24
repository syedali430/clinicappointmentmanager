package com.example.clinicappointmentmanager.controller.racecondition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.clinicappointmentmanager.controller.AppointmentController;
import com.example.clinicappointmentmanager.model.Appointment;
import com.example.clinicappointmentmanager.repository.AppointmentRepository;
import com.example.clinicappointmentmanager.view.AppointmentView;

public class AppointmentControllerRaceConditionTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentView appointmentView;

    @InjectMocks
    private AppointmentController appointmentController;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @After
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void testAddingSameAppointmentAtTheSameTimeAddsOnlyOneAppointment() {
        List<Appointment> appointments = new ArrayList<>();

        Appointment appointment =
                new Appointment(
                        "CLN-001",
                        "Marco Rossi",
                        "Alessandro Moretti",
                        "2026-06-15",
                        "10:30",
                        "General Checkup"
                );

        when(appointmentRepository.findById(anyString()))
                .thenAnswer(invocation ->
                        appointments.stream().findFirst().orElse(null)
                );

        doAnswer(invocation -> {
            appointments.add(appointment);
            return null;
        }).when(appointmentRepository).save(any(Appointment.class));

        List<Thread> threads =
                IntStream.range(0, 10)
                        .mapToObj(i -> new Thread(
                                () -> appointmentController.addAppointment(appointment)
                        ))
                        .peek(Thread::start)
                        .collect(Collectors.toList());

        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .until(() -> threads.stream().noneMatch(Thread::isAlive));

        assertThat(appointments).containsExactly(appointment);
    }

    @Test
    public void testDeletingSameAppointmentAtTheSameTimeRemovesAppointmentOnce() {
        List<Appointment> appointments = new ArrayList<>();

        Appointment appointment =
                new Appointment(
                        "CLN-001",
                        "Marco Rossi",
                        "Alessandro Moretti",
                        "2026-06-15",
                        "10:30",
                        "General Checkup"
                );

        appointments.add(appointment);

        when(appointmentRepository.findById(anyString()))
                .thenAnswer(invocation ->
                        appointments.stream().findFirst().orElse(null)
                );

        doAnswer(invocation -> {
            appointments.remove(appointment);
            return null;
        }).when(appointmentRepository).delete(anyString());

        List<Thread> threads =
                IntStream.range(0, 10)
                        .mapToObj(i -> new Thread(
                                () -> appointmentController.deleteAppointment(appointment)
                        ))
                        .peek(Thread::start)
                        .collect(Collectors.toList());

        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .until(() -> threads.stream().noneMatch(Thread::isAlive));

        assertThat(appointments).isEmpty();
    }

    @Test
    public void testUpdatingSameAppointmentAtSameTimeEndsWithOneCorrectUpdate() {
        List<Appointment> appointments = new ArrayList<>();

        Appointment original =
                new Appointment(
                        "CLN-001",
                        "Marco Rossi",
                        "Alessandro Moretti",
                        "2026-06-15",
                        "10:30",
                        "Old Reason"
                );

        appointments.add(original);

        Appointment updated =
                new Appointment(
                        "CLN-001",
                        "Marco Rossi",
                        "Alessandro Moretti",
                        "2026-06-16",
                        "11:00",
                        "Updated Reason"
                );

        when(appointmentRepository.findById(anyString()))
                .thenAnswer(invocation ->
                        appointments.stream().findFirst().orElse(null)
                );

        doAnswer(invocation -> {
            Appointment appointmentFromController = invocation.getArgument(0);
            appointments.clear();
            appointments.add(appointmentFromController);
            return null;
        }).when(appointmentRepository).update(any(Appointment.class));

        List<Thread> threads =
                IntStream.range(0, 10)
                        .mapToObj(i -> new Thread(
                                () -> appointmentController.updateAppointment(updated)
                        ))
                        .peek(Thread::start)
                        .collect(Collectors.toList());

        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .until(() -> threads.stream().noneMatch(Thread::isAlive));

        assertThat(appointments).containsExactly(updated);
    }
}