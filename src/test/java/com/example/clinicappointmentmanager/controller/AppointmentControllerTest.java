package com.example.clinicappointmentmanager.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.clinicappointmentmanager.model.Appointment;
import com.example.clinicappointmentmanager.repository.AppointmentRepository;
import com.example.clinicappointmentmanager.view.AppointmentView;

public class AppointmentControllerTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentView appointmentView;

    @InjectMocks
    private AppointmentController appointmentController;

    private AutoCloseable closeable;

    @Before
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @After
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void testDisplayAllAppointments() {
        List<Appointment> appointments = asList(new Appointment());
        when(appointmentRepository.findAll()).thenReturn(appointments);

        appointmentController.getAllAppointments();

        verify(appointmentView).displayAppointments(appointments);
    }

    @Test
    public void testDisplayAllAppointmentsWithEmptyList() {
        when(appointmentRepository.findAll()).thenReturn(null);

        appointmentController.getAllAppointments();

        verify(appointmentView).displayAppointments(null);
    }

    @Test
    public void testAddNewAppointmentWhenAppointmentAlreadyExists() {
        Appointment toAdd =
                new Appointment(
                        "CLN-001",
                        "Marco Rossi",
                        "Alessandro Moretti",
                        "2026-06-15",
                        "10:30",
                        "General Checkup"
                );

        Appointment existing =
                new Appointment(
                        "CLN-001",
                        "Marco Rossi",
                        "Alessandro Moretti",
                        "2026-06-15",
                        "10:30",
                        "General Checkup"
                );

        when(appointmentRepository.findById("CLN-001")).thenReturn(existing);

        appointmentController.addAppointment(toAdd);

        verify(appointmentView).showErrorMessage(
                "Appointment already exists with ID CLN-001",
                existing
        );

        verifyNoMoreInteractions(ignoreStubs(appointmentRepository));
    }

    @Test
    public void testAddNewAppointmentWhenAppointmentDoesNotExist() {
        Appointment appointment =
                new Appointment(
                        "CLN-001",
                        "Marco Rossi",
                        "Alessandro Moretti",
                        "2026-06-15",
                        "10:30",
                        "General Checkup"
                );

        when(appointmentRepository.findById("CLN-001")).thenReturn(null);

        appointmentController.addAppointment(appointment);

        InOrder inOrder = inOrder(appointmentRepository, appointmentView);
        inOrder.verify(appointmentRepository).save(appointment);
        inOrder.verify(appointmentView).addAppointment(appointment);
    }

    @Test
    public void testUpdatingExistingAppointment() {
        Appointment appointment =
                new Appointment(
                        "CLN-001",
                        "Marco Rossi",
                        "Alessandro Moretti",
                        "2026-06-15",
                        "10:30",
                        "General Checkup"
                );

        when(appointmentRepository.findById("CLN-001")).thenReturn(appointment);

        appointmentController.updateAppointment(appointment);

        InOrder inOrder = inOrder(appointmentRepository, appointmentView);
        inOrder.verify(appointmentRepository).update(appointment);
        inOrder.verify(appointmentView).updateAppointment(appointment);
    }

    @Test
    public void testUpdatingMissingAppointment() {
        Appointment appointment =
                new Appointment(
                        "CLN-001",
                        "Marco Rossi",
                        "Alessandro Moretti",
                        "2026-06-15",
                        "10:30",
                        "General Checkup"
                );

        when(appointmentRepository.findById("CLN-001")).thenReturn(null);

        appointmentController.updateAppointment(appointment);

        verify(appointmentView).showErrorMessage(
                "No appointment found with ID CLN-001",
                appointment
        );

        verifyNoMoreInteractions(ignoreStubs(appointmentRepository));
    }

    @Test
    public void testDeletingExistingAppointment() {
        Appointment appointment =
                new Appointment(
                        "CLN-001",
                        "Marco Rossi",
                        "Alessandro Moretti",
                        "2026-06-15",
                        "10:30",
                        "General Checkup"
                );

        when(appointmentRepository.findById("CLN-001")).thenReturn(appointment);

        appointmentController.deleteAppointment(appointment);

        InOrder inOrder = inOrder(appointmentRepository, appointmentView);
        inOrder.verify(appointmentRepository).delete("CLN-001");
        inOrder.verify(appointmentView).deleteAppointment(appointment);
    }

    @Test
    public void testDeletingMissingAppointment() {
        Appointment appointment =
                new Appointment(
                        "CLN-001",
                        "Marco Rossi",
                        "Alessandro Moretti",
                        "2026-06-15",
                        "10:30",
                        "General Checkup"
                );

        when(appointmentRepository.findById("CLN-001")).thenReturn(null);

        appointmentController.deleteAppointment(appointment);

        verify(appointmentView).showErrorMessage(
                "No appointment found with ID CLN-001",
                appointment
        );

        verifyNoMoreInteractions(ignoreStubs(appointmentRepository));
    }
}