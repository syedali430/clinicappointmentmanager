package com.example.clinicappointmentmanager.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.awaitility.Awaitility;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.clinicappointmentmanager.controller.AppointmentController;
import com.example.clinicappointmentmanager.model.Appointment;
import com.example.clinicappointmentmanager.repository.mongo.AppointmentMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

@RunWith(GUITestRunner.class)
public class AppointmentSwingViewIT extends AssertJSwingJUnitTestCase {

    private static MongoServer server;
    private static InetSocketAddress serverAddress;

    private MongoClient mongoClient;

    private FrameFixture window;
    private AppointmentSwingView appointmentSwingView;
    private AppointmentController appointmentController;
    private AppointmentMongoRepository appointmentRepository;

    public static final String APPOINTMENT_COLLECTION_NAME = "appointment";
    public static final String APPOINTMENT_DB_NAME = "clinicappointmentmanager";

    @BeforeClass
    public static void setupServer() {
        server = new MongoServer(new MemoryBackend());
        serverAddress = server.bind();
    }

    @AfterClass
    public static void shutdownServer() {
        server.shutdown();
    }

    @Override
    protected void onSetUp() {
        mongoClient = new MongoClient(new ServerAddress(serverAddress));

        appointmentRepository =
                new AppointmentMongoRepository(
                        mongoClient,
                        APPOINTMENT_DB_NAME,
                        APPOINTMENT_COLLECTION_NAME
                );

        for (Appointment appointment : appointmentRepository.findAll()) {
            appointmentRepository.delete(appointment.getAppointmentId());
        }

        GuiActionRunner.execute(() -> {
            appointmentSwingView = new AppointmentSwingView();
            appointmentController =
                    new AppointmentController(
                            appointmentRepository,
                            appointmentSwingView
                    );
            appointmentSwingView.setAppointmentController(appointmentController);
            return appointmentSwingView;
        });

        window = new FrameFixture(robot(), appointmentSwingView);
        window.show();
    }

    @Override
    protected void onTearDown() {
        mongoClient.close();
    }

    @Test
    @GUITest
    public void testAllAppointments() {
        Appointment appointment1 =
                new Appointment(
                        "CLN-001",
                        "Marco Rossi",
                        "Alessandro Moretti",
                        "2026-06-15",
                        "10:30",
                        "General Checkup"
                );

        Appointment appointment2 =
                new Appointment(
                        "CLN-002",
                        "Giulia Bianchi",
                        "Chiara Greco",
                        "2026-06-16",
                        "11:00",
                        "Dental Consultation"
                );

        appointmentRepository.save(appointment1);
        appointmentRepository.save(appointment2);

        GuiActionRunner.execute(() ->
                appointmentController.getAllAppointments()
        );

        assertThat(window.list("appointmentList").contents())
                .containsExactly(
                        appointment1.toString(),
                        appointment2.toString()
                );
    }

    @Test
    @GUITest
    public void testAddButtonSuccess() {
        window.textBox("appointmentidTextBox").enterText("CLN-003");
        window.textBox("patientnameTextBox").enterText("Luca Ferrari");
        window.textBox("doctornameTextBox").enterText("Francesca Marino");
        window.textBox("appointmentdateTextBox").enterText("2026-06-17");
        window.textBox("appointmenttimeTextBox").enterText("12:00");
        window.textBox("reasonTextBox").enterText("Physiotherapy");

        window.button(JButtonMatcher.withText("Add Appointment")).click();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(window.list("appointmentList").contents())
                                .containsExactly(
                                        new Appointment(
                                                "CLN-003",
                                                "Luca Ferrari",
                                                "Francesca Marino",
                                                "2026-06-17",
                                                "12:00",
                                                "Physiotherapy"
                                        ).toString()
                                )
                );
    }

    @Test
    @GUITest
    public void testDeleteButtonSuccess() {
        GuiActionRunner.execute(() ->
                appointmentController.addAppointment(
                        new Appointment(
                                "CLN-004",
                                "Sofia Romano",
                                "Lorenzo Gallo",
                                "2026-06-18",
                                "13:00",
                                "To be deleted"
                        )
                )
        );

        window.list("appointmentList").selectItem(0);
        window.button(JButtonMatcher.withText("Delete Appointment")).click();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(window.list("appointmentList").contents())
                                .isEmpty()
                );
    }

    @Test
    @GUITest
    public void testUpdateButtonSuccess() {
        Appointment appointment =
                new Appointment(
                        "CLN-005",
                        "Matteo Ricci",
                        "Martina Costa",
                        "2026-06-19",
                        "14:00",
                        "Before update"
                );

        appointmentRepository.save(appointment);

        GuiActionRunner.execute(() ->
                appointmentSwingView.getListAppointmentModel()
                        .addElement(appointment)
        );

        window.list("appointmentList").selectItem(0);

        window.textBox("appointmentidTextBox").requireText("CLN-005");
        window.textBox("appointmentidTextBox").requireDisabled();

        window.textBox("patientnameTextBox").setText("Matteo Ricci");
        window.textBox("doctornameTextBox").setText("Martina Costa");
        window.textBox("appointmentdateTextBox").setText("2026-06-20");
        window.textBox("appointmenttimeTextBox").setText("15:00");
        window.textBox("reasonTextBox").setText("After update");

        window.button(JButtonMatcher.withText("Update Appointment")).click();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(window.list("appointmentList").contents())
                                .containsExactly(
                                        new Appointment(
                                                "CLN-005",
                                                "Matteo Ricci",
                                                "Martina Costa",
                                                "2026-06-20",
                                                "15:00",
                                                "After update"
                                        ).toString()
                                )
                );
    }
}