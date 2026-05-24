package com.example.clinicappointmentmanager.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.awaitility.Awaitility;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.example.clinicappointmentmanager.controller.AppointmentController;
import com.example.clinicappointmentmanager.model.Appointment;
import com.example.clinicappointmentmanager.repository.mongo.AppointmentMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@RunWith(GUITestRunner.class)
public class ModelViewControllerIT extends AssertJSwingJUnitTestCase {

    @ClassRule
    public static final MongoDBContainer mongo =
            new MongoDBContainer("mongo:4.4.3");

    private MongoClient mongoClient;

    private FrameFixture window;
    private AppointmentMongoRepository appointmentRepository;
    private AppointmentController appointmentController;

    public static final String APPOINTMENT_COLLECTION_NAME = "appointment";
    public static final String APPOINTMENT_DB_NAME = "clinicappointmentmanager";

    @Override
    protected void onSetUp() {
        mongoClient =
                new MongoClient(
                        new ServerAddress(
                                mongo.getHost(),
                                mongo.getFirstMappedPort()
                        )
                );

        appointmentRepository =
                new AppointmentMongoRepository(
                        mongoClient,
                        APPOINTMENT_DB_NAME,
                        APPOINTMENT_COLLECTION_NAME
                );

        for (Appointment appointment : appointmentRepository.findAll()) {
            appointmentRepository.delete(appointment.getAppointmentId());
        }

        window = new FrameFixture(
                robot(),
                GuiActionRunner.execute(() -> {
                    AppointmentSwingView appointmentSwingView =
                            new AppointmentSwingView();

                    appointmentController =
                            new AppointmentController(
                                    appointmentRepository,
                                    appointmentSwingView
                            );

                    appointmentSwingView.setAppointmentController(
                            appointmentController
                    );

                    return appointmentSwingView;
                })
        );

        window.show();
    }

    @Override
    protected void onTearDown() {
        mongoClient.close();
    }

    @Test
    public void testAddAppointment() {
        window.textBox("appointmentidTextBox").enterText("CLN-001");
        window.textBox("patientnameTextBox").enterText("Marco Rossi");
        window.textBox("doctornameTextBox").enterText("Alessandro Moretti");
        window.textBox("appointmentdateTextBox").enterText("2026-06-15");
        window.textBox("appointmenttimeTextBox").enterText("10:30");
        window.textBox("reasonTextBox").enterText("Appointment added through UI");

        window.button(JButtonMatcher.withText("Add Appointment")).click();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(appointmentRepository.findById("CLN-001"))
                                .isEqualTo(
                                        new Appointment(
                                                "CLN-001",
                                                "Marco Rossi",
                                                "Alessandro Moretti",
                                                "2026-06-15",
                                                "10:30",
                                                "Appointment added through UI"
                                        )
                                )
                );
    }

    @Test
    public void testDeleteAppointment() {
        appointmentRepository.save(
                new Appointment(
                        "CLN-002",
                        "Giulia Bianchi",
                        "Chiara Greco",
                        "2026-06-16",
                        "11:00",
                        "To be deleted"
                )
        );

        GuiActionRunner.execute(() ->
                appointmentController.getAllAppointments()
        );

        window.list("appointmentList").selectItem(0);
        window.button(JButtonMatcher.withText("Delete Appointment")).click();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(appointmentRepository.findById("CLN-002"))
                                .isNull()
                );
    }

    @Test
    public void testUpdateAppointment() {
        appointmentRepository.save(
                new Appointment(
                        "CLN-003",
                        "Luca Ferrari",
                        "Francesca Marino",
                        "2026-06-17",
                        "12:00",
                        "Before update"
                )
        );

        GuiActionRunner.execute(() ->
                appointmentController.getAllAppointments()
        );

        window.list("appointmentList").selectItem(0);

        window.textBox("appointmentidTextBox").requireText("CLN-003");
        window.textBox("appointmentidTextBox").requireDisabled();

        window.textBox("patientnameTextBox").setText("Luca Ferrari");
        window.textBox("doctornameTextBox").setText("Francesca Marino");
        window.textBox("appointmentdateTextBox").setText("2026-06-18");
        window.textBox("appointmenttimeTextBox").setText("13:00");
        window.textBox("reasonTextBox").setText("After update");

        window.button(JButtonMatcher.withText("Update Appointment")).click();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(appointmentRepository.findById("CLN-003"))
                                .isEqualTo(
                                        new Appointment(
                                                "CLN-003",
                                                "Luca Ferrari",
                                                "Francesca Marino",
                                                "2026-06-18",
                                                "13:00",
                                                "After update"
                                        )
                                )
                );

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(window.list("appointmentList").contents())
                                .containsExactly(
                                        new Appointment(
                                                "CLN-003",
                                                "Luca Ferrari",
                                                "Francesca Marino",
                                                "2026-06-18",
                                                "13:00",
                                                "After update"
                                        ).toString()
                                )
                );
    }
}