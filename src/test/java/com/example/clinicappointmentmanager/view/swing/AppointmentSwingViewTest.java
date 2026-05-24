package com.example.clinicappointmentmanager.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.awaitility.Awaitility;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.clinicappointmentmanager.controller.AppointmentController;
import com.example.clinicappointmentmanager.model.Appointment;

@RunWith(GUITestRunner.class)
public class AppointmentSwingViewTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private AppointmentSwingView appointmentSwingView;

    @Mock
    private AppointmentController appointmentController;

    private AutoCloseable closeable;

    private static final String ID = "CLN-001";
    private static final String PATIENT = "Marco Rossi";
    private static final String DOCTOR = "Alessandro Moretti";
    private static final String DATE = "2026-06-15";
    private static final String TIME = "10:30";
    private static final String REASON = "General Checkup";

    private static final Appointment APPOINTMENT =
            new Appointment(ID, PATIENT, DOCTOR, DATE, TIME, REASON);

    @Override
    protected void onSetUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);

        GuiActionRunner.execute(() -> {
            appointmentSwingView = new AppointmentSwingView();
            appointmentSwingView.setAppointmentController(appointmentController);
            return appointmentSwingView;
        });

        window = new FrameFixture(robot(), appointmentSwingView);
        window.show();
    }

    @Override
    protected void onTearDown() throws Exception {
        closeable.close();
    }

    private void typeAllFieldsValidSoAddEnables() {
        window.textBox("appointmentidTextBox").enterText(ID);
        window.textBox("patientnameTextBox").enterText(PATIENT);
        window.textBox("doctornameTextBox").enterText(DOCTOR);
        window.textBox("appointmentdateTextBox").enterText(DATE);
        window.textBox("appointmenttimeTextBox").enterText(TIME);
        window.textBox("reasonTextBox").enterText(REASON);
    }

    private void clickAddAppointment() {
        window.button(JButtonMatcher.withText("Add Appointment")).click();
    }

    private void clickUpdateAppointment() {
        window.button(JButtonMatcher.withText("Update Appointment")).click();
    }

    private void clickDeleteAppointment() {
        window.button(JButtonMatcher.withText("Delete Appointment")).click();
    }

    private void clickClear() {
        window.button(JButtonMatcher.withText("Clear")).click();
    }

    private String makeStringOfLength(int n) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < n; i++) {
            sb.append("s");
        }

        return sb.toString();
    }

    private boolean invokePrivateBooleanMethod(String methodName, String value)
            throws Exception {

        java.lang.reflect.Method method =
                AppointmentSwingView.class.getDeclaredMethod(
                        methodName,
                        String.class
                );

        method.setAccessible(true);

        return ((Boolean) method.invoke(appointmentSwingView, value))
                .booleanValue();
    }

    @Test
    @GUITest
    public void testInitialStateButtonsDisabledFieldsEmptyLabelsCorrectAndAppointmentListEmpty() {
        window.label(JLabelMatcher.withText("Appointment ID")).requireVisible();
        window.label(JLabelMatcher.withText("Patient Name")).requireVisible();
        window.label(JLabelMatcher.withText("Doctor Name")).requireVisible();
        window.label(JLabelMatcher.withText("Appointment Date")).requireVisible();
        window.label(JLabelMatcher.withText("Appointment Time")).requireVisible();
        window.label(JLabelMatcher.withText("Reason")).requireVisible();

        window.textBox("appointmentidTextBox").requireEmpty();
        window.textBox("patientnameTextBox").requireEmpty();
        window.textBox("doctornameTextBox").requireEmpty();
        window.textBox("appointmentdateTextBox").requireEmpty();
        window.textBox("appointmenttimeTextBox").requireEmpty();
        window.textBox("reasonTextBox").requireEmpty();

        window.button("btnAddAppointment").requireDisabled();
        window.button("btnClear").requireDisabled();
        window.button("btnUpdateAppointment").requireDisabled();
        window.button("btnDeleteAppointment").requireDisabled();

        assertThat(window.list("appointmentList").contents()).isEmpty();

        window.label("errorMessageLabel").requireText("");
    }

    @Test
    @GUITest
    public void testWhenAllFieldsAreFilledAddAppointmentButtonBecomesEnabled() {
        typeAllFieldsValidSoAddEnables();

        window.button("btnAddAppointment").requireEnabled();
    }

    @Test
    @GUITest
    public void testWhenAnyFieldIsEmptyAddAppointmentButtonStaysDisabled() {
        window.textBox("appointmentidTextBox").enterText(" ");
        window.textBox("patientnameTextBox").enterText(PATIENT);
        window.textBox("doctornameTextBox").enterText(DOCTOR);
        window.textBox("appointmentdateTextBox").enterText(DATE);
        window.textBox("appointmenttimeTextBox").enterText(TIME);

        window.button("btnAddAppointment").requireDisabled();
    }

    @Test
    @GUITest
    public void testWhenAppointmentIsSelectedFieldsFilledAppointmentIdDisabledUpdateDeleteClearEnabledAndAddDisabled() {
        GuiActionRunner.execute(() ->
                appointmentSwingView.getListAppointmentModel()
                        .addElement(APPOINTMENT)
        );

        window.list("appointmentList").selectItem(0);

        window.textBox("appointmentidTextBox").requireText(ID);
        window.textBox("patientnameTextBox").requireText(PATIENT);
        window.textBox("doctornameTextBox").requireText(DOCTOR);
        window.textBox("appointmentdateTextBox").requireText(DATE);
        window.textBox("appointmenttimeTextBox").requireText(TIME);
        window.textBox("reasonTextBox").requireText(REASON);

        window.textBox("appointmentidTextBox").requireDisabled();

        window.button("btnUpdateAppointment").requireEnabled();
        window.button("btnDeleteAppointment").requireEnabled();
        window.button("btnClear").requireEnabled();
        window.button("btnAddAppointment").requireDisabled();
    }

    @Test
    @GUITest
    public void testWhenClearButtonIsClickedFieldsBecomeEmptyAndSelectionIsCleared() {
        GuiActionRunner.execute(() ->
                appointmentSwingView.getListAppointmentModel()
                        .addElement(APPOINTMENT)
        );

        window.list("appointmentList").selectItem(0);
        clickClear();

        window.textBox("appointmentidTextBox").requireText("");
        window.textBox("patientnameTextBox").requireText("");
        window.textBox("doctornameTextBox").requireText("");
        window.textBox("appointmentdateTextBox").requireText("");
        window.textBox("appointmenttimeTextBox").requireText("");
        window.textBox("reasonTextBox").requireText("");

        assertThat(window.list("appointmentList").selection()).isEmpty();

        window.button("btnUpdateAppointment").requireDisabled();
        window.button("btnDeleteAppointment").requireDisabled();
        window.button("btnAddAppointment").requireDisabled();
    }

    @Test
    @GUITest
    public void testWhenWindowStartsAndAppointmentsAreLoadedTheyAppearInTheList() {
        Appointment appointment2 =
                new Appointment(
                        "CLN-002",
                        "Giulia Bianchi",
                        "Chiara Greco",
                        "2026-06-16",
                        "11:00",
                        "Dental Consultation"
                );

        GuiActionRunner.execute(() ->
                appointmentSwingView.displayAppointments(
                        Arrays.asList(APPOINTMENT, appointment2)
                )
        );

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(window.list("appointmentList").contents())
                                .containsExactly(
                                        APPOINTMENT.toString(),
                                        appointment2.toString()
                                )
                );
    }

    @Test
    @GUITest
    public void testWhenAddAppointmentButtonIsClickedAppointmentIsAddedInTheAppointmentList() {
        typeAllFieldsValidSoAddEnables();
        clickAddAppointment();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(appointmentController)
                                .addAppointment(APPOINTMENT)
                );

        GuiActionRunner.execute(() ->
                appointmentSwingView.addAppointment(APPOINTMENT)
        );

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(window.list("appointmentList").contents())
                                .containsExactly(APPOINTMENT.toString())
                );
    }

    @Test
    @GUITest
    public void testWhenDeleteAppointmentButtonIsClickedAppointmentIsDeletedFromTheAppointmentList() {
        Appointment appointment2 =
                new Appointment(
                        "CLN-002",
                        "Giulia Bianchi",
                        "Chiara Greco",
                        "2026-06-16",
                        "11:00",
                        "Dental Consultation"
                );

        GuiActionRunner.execute(() -> {
            DefaultListModel<Appointment> model =
                    appointmentSwingView.getListAppointmentModel();

            model.addElement(APPOINTMENT);
            model.addElement(appointment2);
        });

        window.list("appointmentList").selectItem(1);
        clickDeleteAppointment();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(appointmentController)
                                .deleteAppointment(appointment2)
                );

        GuiActionRunner.execute(() ->
                appointmentSwingView.deleteAppointment(appointment2)
        );

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(window.list("appointmentList").contents())
                                .containsExactly(APPOINTMENT.toString())
                );
    }

    @Test
    @GUITest
    public void testWhenUpdateAppointmentButtonIsClickedAppointmentIsUpdatedInTheAppointmentList() {
        GuiActionRunner.execute(() ->
                appointmentSwingView.getListAppointmentModel()
                        .addElement(APPOINTMENT)
        );

        window.list("appointmentList").selectItem(0);

        window.textBox("appointmentdateTextBox").setText("");
        window.textBox("appointmentdateTextBox").enterText("2026-06-17");

        window.textBox("appointmenttimeTextBox").setText("");
        window.textBox("appointmenttimeTextBox").enterText("12:00");

        Appointment updated =
                new Appointment(
                        ID,
                        PATIENT,
                        DOCTOR,
                        "2026-06-17",
                        "12:00",
                        REASON
                );

        clickUpdateAppointment();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(appointmentController)
                                .updateAppointment(updated)
                );

        GuiActionRunner.execute(() ->
                appointmentSwingView.updateAppointment(updated)
        );

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(window.list("appointmentList").contents())
                                .containsExactly(updated.toString())
                );
    }

    @Test
    @GUITest
    public void testWhenAppointmentIdIsWrongAppointmentCannotBeAddedToAppointmentListAndErrorMessageIsShown() {
        window.textBox("appointmentidTextBox").enterText("NL+-23");
        window.textBox("patientnameTextBox").enterText(PATIENT);
        window.textBox("doctornameTextBox").enterText(DOCTOR);
        window.textBox("appointmentdateTextBox").enterText(DATE);
        window.textBox("appointmenttimeTextBox").enterText(TIME);
        window.textBox("reasonTextBox").enterText(REASON);

        clickAddAppointment();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        window.label("errorMessageLabel")
                                .requireText(
                                        "Appointment ID must be CLN- followed by 3 digits (e.g., CLN-001)"
                                )
                );

        verify(appointmentController, never()).addAppointment(APPOINTMENT);
    }

    @Test
    @GUITest
    public void testWhenPatientNameHasWrongCharactersAppointmentCannotBeAddedToAppointmentListAndErrorMessageIsShown() {
        window.textBox("appointmentidTextBox").enterText(ID);
        window.textBox("patientnameTextBox").enterText("Marco 123");
        window.textBox("doctornameTextBox").enterText(DOCTOR);
        window.textBox("appointmentdateTextBox").enterText(DATE);
        window.textBox("appointmenttimeTextBox").enterText(TIME);
        window.textBox("reasonTextBox").enterText(REASON);

        clickAddAppointment();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        window.label("errorMessageLabel")
                                .requireText(
                                        "Patient Name must contain only letters and spaces"
                                )
                );

        verify(appointmentController, never()).addAppointment(APPOINTMENT);
    }

    @Test
    @GUITest
    public void testWhenDoctorNameHasWrongCharactersAppointmentCannotBeAddedToAppointmentListAndErrorMessageIsShown() {
        window.textBox("appointmentidTextBox").enterText(ID);
        window.textBox("patientnameTextBox").enterText(PATIENT);
        window.textBox("doctornameTextBox").enterText("Alessandro99");
        window.textBox("appointmentdateTextBox").enterText(DATE);
        window.textBox("appointmenttimeTextBox").enterText(TIME);
        window.textBox("reasonTextBox").enterText(REASON);

        clickAddAppointment();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        window.label("errorMessageLabel")
                                .requireText(
                                        "Doctor Name must contain only letters and spaces"
                                )
                );

        verify(appointmentController, never()).addAppointment(APPOINTMENT);
    }

    @Test
    @GUITest
    public void testWhenAppointmentDateIsWrongAppointmentCannotBeAddedToAppointmentListAndErrorMessageIsShown() {
        window.textBox("appointmentidTextBox").enterText(ID);
        window.textBox("patientnameTextBox").enterText(PATIENT);
        window.textBox("doctornameTextBox").enterText(DOCTOR);
        window.textBox("appointmentdateTextBox").enterText("15-06-2026");
        window.textBox("appointmenttimeTextBox").enterText(TIME);
        window.textBox("reasonTextBox").enterText(REASON);

        clickAddAppointment();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        window.label("errorMessageLabel")
                                .requireText(
                                        "Appointment Date must be in format YYYY-MM-DD"
                                )
                );

        verify(appointmentController, never()).addAppointment(APPOINTMENT);
    }

    @Test
    @GUITest
    public void testWhenAppointmentTimeIsWrongAppointmentCannotBeAddedToAppointmentListAndErrorMessageIsShown() {
        window.textBox("appointmentidTextBox").enterText(ID);
        window.textBox("patientnameTextBox").enterText(PATIENT);
        window.textBox("doctornameTextBox").enterText(DOCTOR);
        window.textBox("appointmentdateTextBox").enterText(DATE);
        window.textBox("appointmenttimeTextBox").enterText("10-30");
        window.textBox("reasonTextBox").enterText(REASON);

        clickAddAppointment();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        window.label("errorMessageLabel")
                                .requireText(
                                        "Appointment Time must be in format HH:MM"
                                )
                );

        verify(appointmentController, never()).addAppointment(APPOINTMENT);
    }

    @Test
    @GUITest
    public void testWhenReasonIsOver200CharactersAppointmentCannotBeAddedToAppointmentListAndErrorMessageIsShown() {
        window.textBox("appointmentidTextBox").enterText(ID);
        window.textBox("patientnameTextBox").enterText(PATIENT);
        window.textBox("doctornameTextBox").enterText(DOCTOR);
        window.textBox("appointmentdateTextBox").enterText(DATE);
        window.textBox("appointmenttimeTextBox").enterText(TIME);
        window.textBox("reasonTextBox").enterText(makeStringOfLength(201));

        clickAddAppointment();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        window.label("errorMessageLabel")
                                .requireText(
                                        "Reason cannot exceed 200 characters"
                                )
                );

        verify(appointmentController, never()).addAppointment(APPOINTMENT);
    }

    @Test
    @GUITest
    public void testUpdateAppointmentDoesNothingWhenFormIsInvalid() {
        GuiActionRunner.execute(() ->
                appointmentSwingView.getListAppointmentModel()
                        .addElement(APPOINTMENT)
        );

        window.list("appointmentList").selectItem(0);

        window.textBox("appointmentidTextBox").requireDisabled();
        window.textBox("patientnameTextBox").setText("123");

        clickUpdateAppointment();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(appointmentController, never())
                                .updateAppointment(any(Appointment.class))
                );
    }

    @Test
    @GUITest
    public void testDeleteAppointmentDoesNothingWhenNoSelection() {
        clickDeleteAppointment();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(appointmentController, never())
                                .deleteAppointment(any(Appointment.class))
                );
    }

    @Test
    @GUITest
    public void testShowErrorMessageWithAppointmentRemovesThatAppointmentFromList() {
        GuiActionRunner.execute(() ->
                appointmentSwingView.getListAppointmentModel()
                        .addElement(APPOINTMENT)
        );

        GuiActionRunner.execute(() ->
                appointmentSwingView.showErrorMessage("Error: ", APPOINTMENT)
        );

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(window.list("appointmentList").contents())
                            .isEmpty();

                    window.label("errorMessageLabel")
                            .requireText("Error: " + APPOINTMENT.toString());
                });
    }

    @Test
    @GUITest
    public void testPrivateValidationMethodsWithNullInput() throws Exception {
        assertThat(invokePrivateBooleanMethod("isAppointmentIdValid", null))
                .isFalse();

        assertThat(invokePrivateBooleanMethod("isLettersAndSpaces", null))
                .isFalse();

        assertThat(invokePrivateBooleanMethod("isDateValid", null))
                .isFalse();

        assertThat(invokePrivateBooleanMethod("isTimeValid", null))
                .isFalse();
    }

    @Test
    @GUITest
    public void testUpdateAppointmentWhenAppointmentIdNotFoundDoesNotChangeList() {
        GuiActionRunner.execute(() ->
                appointmentSwingView.getListAppointmentModel()
                        .addElement(APPOINTMENT)
        );

        Appointment notPresent =
                new Appointment(
                        "CLN-999",
                        "Luca Ferrari",
                        "Chiara Greco",
                        "2026-06-20",
                        "14:00",
                        "Some reason"
                );

        GuiActionRunner.execute(() ->
                appointmentSwingView.updateAppointment(notPresent)
        );

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        assertThat(window.list("appointmentList").contents())
                                .containsExactly(APPOINTMENT.toString())
                );
    }

    @Test
    @GUITest
    public void testDeleteAppointmentClickedWithNoSelectionDoesNotCallController() {
        GuiActionRunner.execute(() ->
                appointmentSwingView.getListAppointmentModel()
                        .addElement(APPOINTMENT)
        );

        window.list("appointmentList").clearSelection();

        GuiActionRunner.execute(() ->
                window.button("btnDeleteAppointment").target().setEnabled(true)
        );

        clickDeleteAppointment();

        Awaitility.await().atMost(2, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(appointmentController, never())
                                .deleteAppointment(any(Appointment.class))
                );
    }

    @Test
    @GUITest
    public void testWhenUpdatingAndPatientNameIsOnlySpacesErrorMessageIsShown() {
        GuiActionRunner.execute(() ->
                appointmentSwingView.getListAppointmentModel()
                        .addElement(APPOINTMENT)
        );

        window.list("appointmentList").selectItem(0);

        window.textBox("patientnameTextBox").setText("   ");

        clickUpdateAppointment();

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        window.label("errorMessageLabel")
                                .requireText(
                                        "Patient Name must contain only letters and spaces"
                                )
                );

        verify(appointmentController, never())
                .updateAppointment(any(Appointment.class));
    }
}