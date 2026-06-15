package com.example.app14;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app14.adapters.EstudianteAdapter;
import com.example.app14.models.Estudiante;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements EstudianteAdapter.OnEstudianteActionListener {

    private EditText etNombre;
    private EditText etEmail;
    private EditText etEdad;
    private DatabaseReference estudiantesRef;
    private EstudianteAdapter estudianteAdapter;
    private final List<Estudiante> estudiantes = new ArrayList<>();
    private String estudianteSeleccionadoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etEdad = findViewById(R.id.etEdad);
        Button btnAgregar = findViewById(R.id.btnAgregar);
        Button btnActualizar = findViewById(R.id.btnActualizar);
        Button btnEliminar = findViewById(R.id.btnEliminar);
        Button btnLimpiar = findViewById(R.id.btnLimpiar);
        RecyclerView rvEstudiantes = findViewById(R.id.rvEstudiantes);

        rvEstudiantes.setLayoutManager(new LinearLayoutManager(this));
        rvEstudiantes.setNestedScrollingEnabled(false);

        estudianteAdapter = new EstudianteAdapter(estudiantes, this);
        rvEstudiantes.setAdapter(estudianteAdapter);

        estudiantesRef = FirebaseDatabase.getInstance().getReference("estudiantes");

        btnAgregar.setOnClickListener(v -> agregarEstudiante());
        btnActualizar.setOnClickListener(v -> actualizarEstudiante());
        btnEliminar.setOnClickListener(v -> eliminarEstudiante());
        btnLimpiar.setOnClickListener(v -> limpiarCampos());

        leerEstudiantes();
    }

    private boolean datosValidos() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String edad = etEdad.getText().toString().trim();

        // Verificar campos obligatorios
        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(email) || TextUtils.isEmpty(edad)) {
            Toast.makeText(this, R.string.msg_campos_obligatorios, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar nombre (mínimo 3 letras)
        if (nombre.length() < 3) {
            Toast.makeText(this, "El nombre debe tener al menos 3 letras", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar email
        if (!isValidEmail(email)) {
            Toast.makeText(this, "El email debe contener @ y terminar en .com", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar edad
        try {
            Integer.parseInt(edad);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.msg_edad_invalida, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        // Patrón para validar: debe contener @ y terminar en .com
        String emailPattern = "^[^@\\s]+@[^@\\s]+\\.com$";
        return Pattern.matches(emailPattern, email);
    }

    private Estudiante obtenerEstudianteFormulario(String id) {
        return new Estudiante(
                id,
                etNombre.getText().toString().trim(),
                etEmail.getText().toString().trim(),
                Integer.parseInt(etEdad.getText().toString().trim())
        );
    }

    private void agregarEstudiante() {
        if (!datosValidos()) {
            return;
        }

        String id = estudiantesRef.push().getKey();
        if (id == null) {
            Toast.makeText(this, R.string.msg_error_generico, Toast.LENGTH_SHORT).show();
            return;
        }

        Estudiante estudiante = obtenerEstudianteFormulario(id);
        estudiantesRef.child(id)
                .setValue(estudiante)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, R.string.msg_agregado, Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                })
                .addOnFailureListener(e -> Toast.makeText(this, R.string.msg_error_generico, Toast.LENGTH_SHORT).show());
    }

    private void leerEstudiantes() {
        estudiantesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                estudiantes.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Estudiante estudiante = dataSnapshot.getValue(Estudiante.class);
                    if (estudiante != null) {
                        if (estudiante.getId() == null) {
                            estudiante.setId(dataSnapshot.getKey());
                        }
                        estudiantes.add(estudiante);
                    }
                }
                estudianteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, R.string.msg_error_lectura, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarEstudiante() {
        if (TextUtils.isEmpty(estudianteSeleccionadoId)) {
            Toast.makeText(this, R.string.msg_selecciona_estudiante, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!datosValidos()) {
            return;
        }

        Estudiante estudiante = obtenerEstudianteFormulario(estudianteSeleccionadoId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("id", estudiante.getId());
        updates.put("nombre", estudiante.getNombre());
        updates.put("email", estudiante.getEmail());
        updates.put("edad", estudiante.getEdad());

        estudiantesRef.child(estudianteSeleccionadoId)
                .updateChildren(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, R.string.msg_actualizado, Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                })
                .addOnFailureListener(e -> Toast.makeText(this, R.string.msg_error_generico, Toast.LENGTH_SHORT).show());
    }

    private void eliminarEstudiante() {
        if (TextUtils.isEmpty(estudianteSeleccionadoId)) {
            Toast.makeText(this, R.string.msg_selecciona_estudiante, Toast.LENGTH_SHORT).show();
            return;
        }

        eliminarEstudiante(estudianteSeleccionadoId);
    }

    private void eliminarEstudiante(String id) {
        estudiantesRef.child(id)
                .removeValue()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, R.string.msg_eliminado, Toast.LENGTH_SHORT).show();
                    if (id.equals(estudianteSeleccionadoId)) {
                        limpiarCampos();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, R.string.msg_error_generico, Toast.LENGTH_SHORT).show());
    }

    private void limpiarCampos() {
        estudianteSeleccionadoId = null;
        etNombre.setText("");
        etEmail.setText("");
        etEdad.setText("");
    }

    @Override
    public void onSelect(Estudiante estudiante) {
        estudianteSeleccionadoId = estudiante.getId();
        etNombre.setText(estudiante.getNombre());
        etEmail.setText(estudiante.getEmail());
        etEdad.setText(String.valueOf(estudiante.getEdad()));
    }

    @Override
    public void onEdit(Estudiante estudiante) {
        onSelect(estudiante);
        Toast.makeText(this, R.string.msg_editar_estudiante, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDelete(Estudiante estudiante) {
        if (estudiante.getId() != null) {
            eliminarEstudiante(estudiante.getId());
        }
    }
}
