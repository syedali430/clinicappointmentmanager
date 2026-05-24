package com.example.clinicappointmentmanager.model;

import java.util.Objects;

public class Appointment {

    private String appointmentId;
    private String patientName;
    private String doctorName;
    private String appointmentDate;
    private String appointmentTime;
    private String reason;

    public Appointment() {
    }

    public Appointment(String appointmentId, String patientName, String doctorName,
            String appointmentDate, String appointmentTime, String reason) {
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Appointment appointment = (Appointment) o;

        return Objects.equals(appointmentId, appointment.appointmentId)
                && Objects.equals(patientName, appointment.patientName)
                && Objects.equals(doctorName, appointment.doctorName)
                && Objects.equals(appointmentDate, appointment.appointmentDate)
                && Objects.equals(appointmentTime, appointment.appointmentTime)
                && Objects.equals(reason, appointment.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appointmentId, patientName, doctorName, appointmentDate, appointmentTime, reason);
    }

    @Override
    public String toString() {
        return appointmentId + " - " + patientName + " - " + doctorName + " - "
                + appointmentDate + " - " + appointmentTime + " - " + reason;
    }
}