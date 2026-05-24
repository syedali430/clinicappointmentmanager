package com.example.clinicappointmentmanager.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.example.clinicappointmentmanager.model.Appointment;
import com.example.clinicappointmentmanager.repository.AppointmentRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class AppointmentMongoRepository implements AppointmentRepository {

    private final MongoCollection<Document> appointmentCollection;

    private static final String ID = "appointmentId";
    private static final String PATIENT = "patientName";
    private static final String DOCTOR = "doctorName";
    private static final String DATE = "appointmentDate";
    private static final String TIME = "appointmentTime";
    private static final String REASON = "reason";

    public AppointmentMongoRepository(
            MongoClient client,
            String databaseName,
            String collectionName) {

        this.appointmentCollection =
                client.getDatabase(databaseName)
                        .getCollection(collectionName);
    }

    @Override
    public void save(Appointment appointment) {

        appointmentCollection.insertOne(
                new Document()
                        .append(ID, appointment.getAppointmentId())
                        .append(PATIENT, appointment.getPatientName())
                        .append(DOCTOR, appointment.getDoctorName())
                        .append(DATE, appointment.getAppointmentDate())
                        .append(TIME, appointment.getAppointmentTime())
                        .append(REASON, appointment.getReason())
        );
    }

    @Override
    public List<Appointment> findAll() {

        return StreamSupport
                .stream(appointmentCollection.find().spliterator(), false)
                .map(this::fromDocument)
                .collect(Collectors.toList());
    }

    @Override
    public Appointment findById(String appointmentId) {

        Document d =
                appointmentCollection.find(Filters.eq(ID, appointmentId))
                        .first();

        return d != null ? fromDocument(d) : null;
    }

    @Override
    public void update(Appointment appointment) {

        Document update =
                new Document()
                        .append(PATIENT, appointment.getPatientName())
                        .append(DOCTOR, appointment.getDoctorName())
                        .append(DATE, appointment.getAppointmentDate())
                        .append(TIME, appointment.getAppointmentTime())
                        .append(REASON, appointment.getReason());

        appointmentCollection.updateOne(
                Filters.eq(ID, appointment.getAppointmentId()),
                new Document("$set", update)
        );
    }

    @Override
    public void delete(String appointmentId) {

        appointmentCollection.deleteOne(
                Filters.eq(ID, appointmentId)
        );
    }

    private Appointment fromDocument(Document d) {

        return new Appointment(
                "" + d.get(ID),
                "" + d.get(PATIENT),
                "" + d.get(DOCTOR),
                "" + d.get(DATE),
                "" + d.get(TIME),
                "" + d.get(REASON)
        );
    }
}