package com.example.clinicappointmentmanager.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.example.clinicappointmentmanager.model.Appointment;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class AppointmentMongoRepositoryTestcontainersIT {

    @ClassRule
    public static final MongoDBContainer mongo =
            new MongoDBContainer("mongo:4.4.3");

    private MongoClient client;
    private AppointmentMongoRepository appointmentRepository;
    private MongoCollection<Document> appointmentCollection;

    public static final String APPOINTMENT_COLLECTION_NAME = "appointment";
    public static final String APPOINTMENT_DB_NAME = "clinicappointmentmanager";

    @Before
    public void setup() {
        client =
                new MongoClient(
                        new ServerAddress(
                                mongo.getHost(),
                                mongo.getFirstMappedPort()
                        )
                );

        appointmentRepository =
                new AppointmentMongoRepository(
                        client,
                        APPOINTMENT_DB_NAME,
                        APPOINTMENT_COLLECTION_NAME
                );

        MongoDatabase database =
                client.getDatabase(APPOINTMENT_DB_NAME);

        database.drop();

        appointmentCollection =
                database.getCollection(APPOINTMENT_COLLECTION_NAME);
    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void testFindAll() {
        addTestAppointmentToDatabase(
                "CLN-001",
                "Marco Rossi",
                "Alessandro Moretti",
                "2026-06-15",
                "10:30",
                "General Checkup"
        );

        addTestAppointmentToDatabase(
                "CLN-002",
                "Giulia Bianchi",
                "Chiara Greco",
                "2026-06-16",
                "11:00",
                "Dental Consultation"
        );

        assertThat(appointmentRepository.findAll())
                .containsExactly(
                        new Appointment(
                                "CLN-001",
                                "Marco Rossi",
                                "Alessandro Moretti",
                                "2026-06-15",
                                "10:30",
                                "General Checkup"
                        ),
                        new Appointment(
                                "CLN-002",
                                "Giulia Bianchi",
                                "Chiara Greco",
                                "2026-06-16",
                                "11:00",
                                "Dental Consultation"
                        )
                );
    }

    @Test
    public void testFindById() {
        addTestAppointmentToDatabase(
                "CLN-001",
                "Marco Rossi",
                "Alessandro Moretti",
                "2026-06-15",
                "10:30",
                "General Checkup"
        );

        addTestAppointmentToDatabase(
                "CLN-002",
                "Giulia Bianchi",
                "Chiara Greco",
                "2026-06-16",
                "11:00",
                "Dental Consultation"
        );

        assertThat(appointmentRepository.findById("CLN-002"))
                .isEqualTo(
                        new Appointment(
                                "CLN-002",
                                "Giulia Bianchi",
                                "Chiara Greco",
                                "2026-06-16",
                                "11:00",
                                "Dental Consultation"
                        )
                );
    }

    @Test
    public void testSave() {
        Appointment appointment =
                new Appointment(
                        "CLN-003",
                        "Luca Ferrari",
                        "Francesca Marino",
                        "2026-06-17",
                        "12:00",
                        "Physiotherapy"
                );

        appointmentRepository.save(appointment);

        assertThat(readAllAppointmentsFromDatabase())
                .containsExactly(appointment);
    }

    @Test
    public void testDelete() {
        addTestAppointmentToDatabase(
                "CLN-004",
                "Sofia Romano",
                "Lorenzo Gallo",
                "2026-06-18",
                "13:00",
                "To be deleted"
        );

        appointmentRepository.delete("CLN-004");

        assertThat(readAllAppointmentsFromDatabase()).isEmpty();
    }

    @Test
    public void testUpdate() {
        Appointment original =
                new Appointment(
                        "CLN-005",
                        "Matteo Ricci",
                        "Martina Costa",
                        "2026-06-19",
                        "14:00",
                        "Before update"
                );

        appointmentRepository.save(original);

        assertThat(readAllAppointmentsFromDatabase())
                .containsExactly(original);

        Appointment updated =
                new Appointment(
                        "CLN-005",
                        "Matteo Ricci",
                        "Martina Costa",
                        "2026-06-20",
                        "15:00",
                        "After update"
                );

        appointmentRepository.update(updated);

        assertThat(readAllAppointmentsFromDatabase())
                .containsExactly(updated);
    }

    private void addTestAppointmentToDatabase(
            String appointmentId,
            String patientName,
            String doctorName,
            String appointmentDate,
            String appointmentTime,
            String reason) {

        appointmentCollection.insertOne(
                new Document()
                        .append("appointmentId", appointmentId)
                        .append("patientName", patientName)
                        .append("doctorName", doctorName)
                        .append("appointmentDate", appointmentDate)
                        .append("appointmentTime", appointmentTime)
                        .append("reason", reason)
        );
    }

    private List<Appointment> readAllAppointmentsFromDatabase() {
        return StreamSupport
                .stream(appointmentCollection.find().spliterator(), false)
                .map(d -> new Appointment(
                        "" + d.get("appointmentId"),
                        "" + d.get("patientName"),
                        "" + d.get("doctorName"),
                        "" + d.get("appointmentDate"),
                        "" + d.get("appointmentTime"),
                        "" + d.get("reason")
                ))
                .collect(Collectors.toList());
    }
}