package com.example.clinicappointmentmanager.repository.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.example.clinicappointmentmanager.model.Appointment;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class AppointmentMongoRepositoryTest {

    private static MongoServer server;
    private static InetSocketAddress serverAddress;

    private MongoClient client;
    private AppointmentMongoRepository appointmentMongoRepository;
    private MongoCollection<Document> appointmentCollection;

    public static final String APPOINTMENT_COLLECTION_NAME = "appointment";
    public static final String APPOINTMENTMANAGER_DB_NAME = "clinicappointmentmanager";

    @BeforeClass
    public static void setupServer() {
        server = new MongoServer(new MemoryBackend());
        serverAddress = server.bind();
    }

    @AfterClass
    public static void shutdownServer() {
        server.shutdown();
    }

    @Before
    public void setup() {
        client = new MongoClient(new ServerAddress(serverAddress));

        appointmentMongoRepository =
                new AppointmentMongoRepository(
                        client,
                        APPOINTMENTMANAGER_DB_NAME,
                        APPOINTMENT_COLLECTION_NAME
                );

        MongoDatabase database =
                client.getDatabase(APPOINTMENTMANAGER_DB_NAME);

        database.drop();

        appointmentCollection =
                database.getCollection(APPOINTMENT_COLLECTION_NAME);
    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void testFindAllWhenDatabaseIsEmpty() {
        assertThat(appointmentMongoRepository.findAll()).isEmpty();
    }

    @Test
    public void testFindAllWhenDatabaseIsNotEmpty() {
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

        assertThat(appointmentMongoRepository.findAll())
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
    public void testFindByIdNotFound() {
        assertThat(appointmentMongoRepository.findById("CLN-001")).isNull();
    }

    @Test
    public void testFindByIdFound() {
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

        assertThat(appointmentMongoRepository.findById("CLN-002"))
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
                        "CLN-001",
                        "Marco Rossi",
                        "Alessandro Moretti",
                        "2026-06-15",
                        "10:30",
                        "General Checkup"
                );

        appointmentMongoRepository.save(appointment);

        assertThat(readAllAppointmentsFromDatabase())
                .containsExactly(appointment);
    }

    @Test
    public void testDelete() {
        addTestAppointmentToDatabase(
                "CLN-001",
                "Marco Rossi",
                "Alessandro Moretti",
                "2026-06-15",
                "10:30",
                "General Checkup"
        );

        appointmentMongoRepository.delete("CLN-001");

        assertThat(readAllAppointmentsFromDatabase()).isEmpty();
    }

    @Test
    public void testUpdate() {
        Appointment original =
                new Appointment(
                        "CLN-001",
                        "Marco Rossi",
                        "Alessandro Moretti",
                        "2026-06-15",
                        "10:30",
                        "General Checkup"
                );

        appointmentMongoRepository.save(original);

        assertThat(readAllAppointmentsFromDatabase())
                .containsExactly(original);

        Appointment updated =
                new Appointment(
                        "CLN-001",
                        "Marco Rossi",
                        "Alessandro Moretti",
                        "2026-06-17",
                        "12:00",
                        "Updated Reason"
                );

        appointmentMongoRepository.update(updated);

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