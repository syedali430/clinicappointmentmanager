package com.example.clinicappointmentmanager.view;

import java.util.List;

import com.example.clinicappointmentmanager.model.Appointment;

public interface AppointmentView {

    void displayAppointments(List<Appointment> appointments);

    void addAppointment(Appointment appointment);

    void deleteAppointment(Appointment appointment);

    void updateAppointment(Appointment appointment);

    void showErrorMessage(String message, Appointment appointment);
}