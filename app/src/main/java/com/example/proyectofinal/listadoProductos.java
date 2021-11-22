package com.example.proyectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class listadoProductos extends AppCompatActivity {
    ListView listadoProductos;

    ArrayList<String> lista = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_productos);


        listadoProductos = findViewById(R.id.listviewProductos);

        mostrarProductos(getApplicationContext());


        listadoProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(listadoProductos.this, modificarProductos.class);
                String t = (String) listadoProductos.getItemAtPosition(position);
                intent.putExtra("valorID", t);
                startActivity(intent);
            }
        });

    }

    public void mostrarProductos(final Context context) {

        lista = new ArrayList<String>();
        String url = "https://coarser-coughs.000webhostapp.com/getAllProductos.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                ArrayList<String> values = new ArrayList<String>();


                try {

                    JSONArray array = new JSONArray(response);


                    for (int i = 0; i < array.length(); i++) {

                        JSONObject productosObject = array.getJSONObject(i);
                        int id_categoria = Integer.parseInt(productosObject.getString("id"));
                        String nombre_categoria = productosObject.getString("nombre");
                        double precio = Double.parseDouble(productosObject.getString("precio"));


                        values.add(String.valueOf(id_categoria) + " - " + nombre_categoria );

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, values);
                        listadoProductos.setAdapter(adapter);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR EN LA CONEXION DE INTERNET", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(context).addToRequestQueue(request);
    }
}