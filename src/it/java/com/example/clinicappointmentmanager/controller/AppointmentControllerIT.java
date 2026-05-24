package com.example.clinicappointmentmanager.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;

import com.example.clinicappointmentmanager.model.Appointment;
import com.example.clinicappointmentmanager.repository.AppointmentRepository;
import com.example.clinicappointmentmanager.repository.mongo.AppointmentMongoRepository;
import com.example.clinicappointmentmanager.view.AppointmentView;
import com.mongodb.MongoClient;

public class AppointmentControllerIT {

    @ClassRule
    public static final MongoDBContainer mongo =
            new MongoDBContainer("mongo:4.4.3");

    @Mock
    private AppointmentView appointmentView;

    private AppointmentRepository appointmentRepository;

    private AppointmentController appointmentController;

    private AutoCloseable closeable;

    public static final String APPOINTMENT_COLLECTION_NAME = "appointment";
    public static final String APPOINTMENT_DB_NAME = "clinicappointmentmanager";

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        appointmentRepository =
                new AppointmentMongoRepository(
                        new MongoClient(
                                mongo.getHost(),
                                mongo.getFirstMappedPort()
                        ),
                        APPOINTMENT_DB_NAME,
                        APPOINTMENT_COLLECTION_NAME
                );

        for (Appointment appointment : appointmentRepository.findAll()) {
            appointmentRepository.delete(appointment.getAppointmentId());
        }

        appointmentController =
                new AppointmentController(
                        appointmentRepository,
                        appointmentView
                );
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testAllAppointmentsAreShownInTheView() {
        Appointment appointment =
                new Appointment(
                        "CLN-001",
                        "Marco Rossi",
                        "Alessandro Moretti",
                        "2026-06-15",
                        "10:30",
                        "Appointment for IT test"
                );

        appointmentRepository.save(appointment);

        appointmentController.getAllAppointments();

        verify(appointmentView).displayAppointments(asList(appointment));
    }

    @Test
    public void testNewAppointmentIsAddedToTheView() {
        Appointment appointment =
                new Appointment(
                        "CLN-002",
                        "Giulia Bianchi",
                        "Chiara Greco",
                        "2026-06-16",
                        "11:00",
                        "New appointment"
                );

        appointmentController.addAppointment(appointment);

        verify(appointmentView).addAppointment(appointment);
    }

    @Test
    public void testSelectedAppointmentIsDeletedFromTheView() {
        Appointment appointmentToDelete =
                new Appointment(
                        "CLN-003",
                        "Luca Ferrari",
                        "Francesca Marino",
                        "2026-06-17",
                        "12:00",
                        "To be deleted"
                );

        appointmentRepository.save(appointmentToDelete);

        appointmentController.deleteAppointment(appointmentToDelete);

        verify(appointmentView).deleteAppointment(appointmentToDelete);
    }

    @Test
    public void testSelectedAppointmentIsUpdatedInTheView() {
        Appointment original =
                new Appointment(
                        "CLN-004",
                        "Sofia Romano",
                        "Lorenzo Gallo",
                        "2026-06-18",
                        "13:00",
                        "Before update"
                );

        appointmentRepository.save(original);

        Appointment updated =
                new Appointment(
                        "CLN-004",
                        "Sofia Romano",
                        "Lorenzo Gallo",
                        "2026-06-19",
                        "14:00",
                        "After update"
                );

        appointmentController.updateAppointment(updated);

        verify(appointmentView).updateAppointment(updated);
    }
}