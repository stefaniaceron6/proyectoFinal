package com.example.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class modificarCategorias extends AppCompatActivity {
    private EditText idC, nombre;
    Spinner estado;
    String idCategoria = "";
    String datoSelected = "";
    private Button actualizar, eliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_categorias);

        Bundle bundle = new Bundle();

        String id = getIntent().getStringExtra("valorC");

        String s[] = id.split(" - ");

        idCategoria = s[0].trim();

        idC = findViewById(R.id.id_cat);
        nombre = findViewById(R.id.nom_cat);
        actualizar = findViewById(R.id.btnUpdate);
        eliminar = findViewById(R.id.btnDelete);
        estado = findViewById(R.id.estado_cat);

        mostrarinfoCategorias(getApplicationContext(), idCategoria);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.estadoCategorias, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        estado.setAdapter(adapter);

        estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(estado.getSelectedItemPosition() > 0){
                    datoSelected = estado.getSelectedItem().toString();
                }else {
                    datoSelected = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idC.getText().toString().trim();
                String Nombre = nombre.getText().toString();

                actualizarCategoria(getApplicationContext(), id, Nombre, datoSelected);
            }
        });


        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarCategoria(getApplicationContext(), idCategoria);
            }
        });


    }


    private void actualizarCategoria (final Context context, String id, String nombre, String estado) {
        String url = "https://coarser-coughs.000webhostapp.com/actualizarCategoria.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject requestJSON = null;
                try {
                    requestJSON = new JSONObject(response.toString());
                    String estado = requestJSON.getString("estado");
                    String mensaje = requestJSON.getString("mensaje");

                    if(estado.equals("1")){
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(modificarCategorias.this, listadoCategorias.class);
                        startActivity(intent);
                        finish();

                    }else if(estado.equals("2")){
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(context, "Error: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error en la conexion" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  map = new HashMap<>();
                map.put("Content-Type", "application/json; charset=utf-8");
                map.put("Accept", "application/json");
                map.put("id_cat", String.valueOf(id));
                map.put("nom_cat", nombre);
                map.put("est_cat", String.valueOf(estado));
                return map;
            }

        };
        MySingleton.getInstance(context).addToRequestQueue(request);


    }

    private void borrarCategoria (Context context, String id_cat){
        String url = "https://coarser-coughs.000webhostapp.com/eliminarCategoria.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                JSONObject requestJSON;
                try {
                    requestJSON = new JSONObject(response.toString());
                    String estado = requestJSON.getString("estado");
                    String mensaje = requestJSON.getString("mensaje");

                    if(estado.equals("1")){
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(modificarCategorias.this, listadoCategorias.class);
                        startActivity(intent);
                        finish();
                    }else if(estado.equals("2")){
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No se Puede Modificar. \n" + "Intentelo Más Tarde.", Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  map = new HashMap<>();
                map.put("Content-Type", "application/json; charset=utf-8");
                map.put("Accept", "application/json");
                map.put("id_cat", String.valueOf(id_cat));
                return map;

            }

        };

        MySingleton.getInstance(context).addToRequestQueue(request);

    }




    private void mostrarinfoCategorias(Context context, String id) {

        String url = "https://coarser-coughs.000webhostapp.com/buscarCategoriaPorCodigo.php";


        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject productosObject = new JSONObject(response);

                    String id = productosObject.getString("id");
                    String nombreC = productosObject.getString("nombre");


                    idC.setText(id);
                    nombre.setText(nombreC);

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error en el try catch" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error en la Conexion", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/json; charset=utf-8");
                map.put("Accept", "application/json");
                map.put("id", id.trim());

                return map;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(request);
    }
}