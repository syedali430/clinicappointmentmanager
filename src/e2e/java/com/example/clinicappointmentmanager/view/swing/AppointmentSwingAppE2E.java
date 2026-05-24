package com.example.clinicappointmentmanager.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

@RunWith(GUITestRunner.class)
public class AppointmentSwingAppE2E extends AssertJSwingJUnitTestCase {

    @ClassRule
    public static final MongoDBContainer mongo =
            new MongoDBContainer("mongo:4.4.3");

    private static final String DB_NAME = "test-db";
    private static final String COLLECTION_NAME = "test-collection";

    private static final String APPOINTMENT_FIXTURE_1_ID = "CLN-001";
    private static final String APPOINTMENT_FIXTURE_1_PATIENT = "Marco Rossi";
    private static final String APPOINTMENT_FIXTURE_1_DOCTOR = "Alessandro Moretti";
    private static final String APPOINTMENT_FIXTURE_1_DATE = "2026-06-15";
    private static final String APPOINTMENT_FIXTURE_1_TIME = "10:30";
    private static final String APPOINTMENT_FIXTURE_1_REASON = "General Checkup";

    private static final String APPOINTMENT_FIXTURE_2_ID = "CLN-002";
    private static final String APPOINTMENT_FIXTURE_2_PATIENT = "Giulia Bianchi";
    private static final String APPOINTMENT_FIXTURE_2_DOCTOR = "Chiara Greco";
    private static final String APPOINTMENT_FIXTURE_2_DATE = "2026-06-16";
    private static final String APPOINTMENT_FIXTURE_2_TIME = "11:00";
    private static final String APPOINTMENT_FIXTURE_2_REASON = "Dental Consultation";

    private MongoClient mongoClient;
    private FrameFixture window;

    @Override
    protected void onSetUp() throws Exception {
        String containerIpAddress = mongo.getContainerIpAddress();
        Integer mappedPort = mongo.getFirstMappedPort();

        mongoClient = new MongoClient(containerIpAddress, mappedPort);
        mongoClient.getDatabase(DB_NAME).drop();

        addTestAppointmentToDatabase(
                APPOINTMENT_FIXTURE_1_ID,
                APPOINTMENT_FIXTURE_1_PATIENT,
                APPOINTMENT_FIXTURE_1_DOCTOR,
                APPOINTMENT_FIXTURE_1_DATE,
                APPOINTMENT_FIXTURE_1_TIME,
                APPOINTMENT_FIXTURE_1_REASON
        );

        addTestAppointmentToDatabase(
                APPOINTMENT_FIXTURE_2_ID,
                APPOINTMENT_FIXTURE_2_PATIENT,
                APPOINTMENT_FIXTURE_2_DOCTOR,
                APPOINTMENT_FIXTURE_2_DATE,
                APPOINTMENT_FIXTURE_2_TIME,
                APPOINTMENT_FIXTURE_2_REASON
        );

        application("com.example.clinicappointmentmanager.app.swing.AppointmentSwingApp")
                .withArgs(
                        "--mongo-host=" + containerIpAddress,
                        "--mongo-port=" + mappedPort.toString(),
                        "--db-name=" + DB_NAME,
                        "--db-collection=" + COLLECTION_NAME
                )
                .start();

        window = WindowFinder.findFrame(
                new GenericTypeMatcher<JFrame>(JFrame.class) {
                    @Override
                    protected boolean isMatching(JFrame frame) {
                        return "Clinic Appointment Manager".equals(frame.getTitle())
                                && frame.isShowing();
                    }
                }
        ).using(robot());
    }

    @Override
    protected void onTearDown() {
        mongoClient.close();
    }

    @Test
    @GUITest
    public void testOnStartAllDatabaseElementsAreShown() {
        assertThat(window.list("appointmentList").contents())
                .anySatisfy(e ->
                        assertThat(e).contains(
                                APPOINTMENT_FIXTURE_1_ID,
                                APPOINTMENT_FIXTURE_1_PATIENT,
                                APPOINTMENT_FIXTURE_1_DOCTOR,
                                APPOINTMENT_FIXTURE_1_DATE,
                                APPOINTMENT_FIXTURE_1_TIME,
                                APPOINTMENT_FIXTURE_1_REASON
                        )
                )
                .anySatisfy(e ->
                        assertThat(e).contains(
                                APPOINTMENT_FIXTURE_2_ID,
                                APPOINTMENT_FIXTURE_2_PATIENT,
                                APPOINTMENT_FIXTURE_2_DOCTOR,
                                APPOINTMENT_FIXTURE_2_DATE,
                                APPOINTMENT_FIXTURE_2_TIME,
                                APPOINTMENT_FIXTURE_2_REASON
                        )
                );
    }

    @Test
    @GUITest
    public void testAddButtonSuccess() {
        window.textBox("appointmentidTextBox").enterText("CLN-010");
        window.textBox("patientnameTextBox").enterText("Luca Ferrari");
        window.textBox("doctornameTextBox").enterText("Francesca Marino");
        window.textBox("appointmentdateTextBox").enterText("2026-06-17");
        window.textBox("appointmenttimeTextBox").enterText("12:00");
        window.textBox("reasonTextBox").enterText("Physiotherapy");

        window.button(JButtonMatcher.withText("Add Appointment")).click();

        assertThat(window.list("appointmentList").contents())
                .anySatisfy(e ->
                        assertThat(e).contains(
                                "CLN-010",
                                "Luca Ferrari",
                                "Francesca Marino",
                                "2026-06-17",
                                "12:00",
                                "Physiotherapy"
                        )
                );
    }

    @Test
    @GUITest
    public void testAddButtonError() {
        window.textBox("appointmentidTextBox")
                .enterText(APPOINTMENT_FIXTURE_1_ID);
        window.textBox("patientnameTextBox").enterText("Nuovo Paziente");
        window.textBox("doctornameTextBox").enterText("Nuovo Dottore");
        window.textBox("appointmentdateTextBox").enterText("2026-06-20");
        window.textBox("appointmenttimeTextBox").enterText("15:00");
        window.textBox("reasonTextBox").enterText("New reason");

        window.button(JButtonMatcher.withText("Add Appointment")).click();

        assertThat(window.label("errorMessageLabel").text())
                .contains(
                        APPOINTMENT_FIXTURE_1_ID,
                        APPOINTMENT_FIXTURE_1_PATIENT,
                        APPOINTMENT_FIXTURE_1_DOCTOR,
                        APPOINTMENT_FIXTURE_1_DATE,
                        APPOINTMENT_FIXTURE_1_TIME,
                        APPOINTMENT_FIXTURE_1_REASON
                );
    }

    @Test
    @GUITest
    public void testDeleteButtonSuccess() {
        window.list("appointmentList")
                .selectItem(Pattern.compile(
                        ".*" + APPOINTMENT_FIXTURE_1_PATIENT + ".*"
                ));

        window.button(JButtonMatcher.withText("Delete Appointment")).click();

        assertThat(window.list("appointmentList").contents())
                .noneMatch(e -> e.contains(APPOINTMENT_FIXTURE_1_PATIENT));
    }

    @Test
    @GUITest
    public void testDeleteButtonError() {
        window.list("appointmentList")
                .selectItem(Pattern.compile(
                        ".*" + APPOINTMENT_FIXTURE_1_PATIENT + ".*"
                ));

        removeTestAppointmentFromDatabase(APPOINTMENT_FIXTURE_1_ID);

        window.button(JButtonMatcher.withText("Delete Appointment")).click();

        assertThat(window.label("errorMessageLabel").text())
                .contains(
                        APPOINTMENT_FIXTURE_1_ID,
                        APPOINTMENT_FIXTURE_1_PATIENT,
                        APPOINTMENT_FIXTURE_1_DOCTOR,
                        APPOINTMENT_FIXTURE_1_DATE,
                        APPOINTMENT_FIXTURE_1_TIME,
                        APPOINTMENT_FIXTURE_1_REASON
                );
    }

    @Test
    @GUITest
    public void testUpdateButtonSuccess() {
        window.list("appointmentList")
                .selectItem(Pattern.compile(
                        ".*" + APPOINTMENT_FIXTURE_1_PATIENT + ".*"
                ));

        window.textBox("patientnameTextBox").setText("Marco Rossi");
        window.textBox("doctornameTextBox").setText("Alessandro Moretti");
        window.textBox("appointmentdateTextBox").setText("2026-06-21");
        window.textBox("appointmenttimeTextBox").setText("16:00");
        window.textBox("reasonTextBox").setText("Updated reason");

        window.button(JButtonMatcher.withText("Update Appointment")).click();

        assertThat(window.list("appointmentList").contents())
                .anySatisfy(e ->
                        assertThat(e).contains(
                                "Marco Rossi",
                                "Alessandro Moretti",
                                "2026-06-21",
                                "16:00",
                                "Updated reason"
                        )
                );
    }

    private void addTestAppointmentToDatabase(
            String appointmentId,
            String patientName,
            String doctorName,
            String appointmentDate,
            String appointmentTime,
            String reason) {

        mongoClient.getDatabase(DB_NAME)
                .getCollection(COLLECTION_NAME)
                .insertOne(
                        new Document()
                                .append("appointmentId", appointmentId)
                                .append("patientName", patientName)
                                .append("doctorName", doctorName)
                                .append("appointmentDate", appointmentDate)
                                .append("appointmentTime", appointmentTime)
                                .append("reason", reason)
                );
    }

    private void removeTestAppointmentFromDatabase(String appointmentId) {
        mongoClient
                .getDatabase(DB_NAME)
                .getCollection(COLLECTION_NAME)
                .deleteOne(Filters.eq("appointmentId", appointmentId));
    }
}