package com.example.proyectofinal.ui.categorias;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.proyectofinal.MySingleton;
import com.example.proyectofinal.R;
import com.example.proyectofinal.databinding.FragmentCategoriasBinding;
import com.example.proyectofinal.listadoCategorias;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CategoriasFragment extends Fragment {
    private CategoriasViewModel categoriasViewModel;
    private FragmentCategoriasBinding binding;

    private EditText id, nombre;
    private Spinner estado;
    private Button save, list;
    String datoSelected = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_categorias, container, false);

        id = root.findViewById(R.id.id_cat);
        nombre = root.findViewById(R.id.nom_cat);
        estado = root.findViewById(R.id.estado_cat);
        save = root.findViewById(R.id.btnSave);
        list = root.findViewById(R.id.btnList);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.estadoCategorias, R.layout.support_simple_spinner_dropdown_item);
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = id.getText().toString();
                String name = nombre.getText().toString();

                if (validarDatos(code, name)) { // Funcion que retorna true si hay datos ingresados
                    if (estado.getSelectedItemPosition() > 0) {

                        // Metodo que guarda  la informacion en la base de datos
                        guardarCategoria(getContext(), Integer.parseInt(code), name, Integer.parseInt(datoSelected));


                    }
                }
            }
        });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), listadoCategorias.class);
                startActivity(intent);
            }
        });




        return root;
    }

    private void guardarCategoria(Context context, int id, String nombre, int estado) {
        String url = "https://coarser-coughs.000webhostapp.com/guardarCategoria.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject requestJSON;
                try {
                    requestJSON = new JSONObject(response.toString());
                    String estado = requestJSON.getString("estado");
                    String mensaje = requestJSON.getString("mensaje");

                    if(estado.equals("1")){
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Conectate a Internet", Toast.LENGTH_SHORT).show();
            }
        })  {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  map = new HashMap<>();
                map.put("Content-Type", "application/json; charset=utf-8");
                map.put("Accept", "application/json");
                map.put("id", String.valueOf(id));
                map.put("nombre", nombre);
                map.put("estado", String.valueOf(estado));
                return map;

            }
        };
        MySingleton.getInstance(context).addToRequestQueue(request);
    }


    public boolean validarDatos(String code, String name) {
        if (code.length() == 0) {
            id.setError("Ingrese un ID");

        } else if( nombre.length() == 0) {
            nombre.setError("Ingrese un Nombre");
        } else if (estado.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Debe seleccionar un estado para la categoria", Toast.LENGTH_SHORT).show();
        }
        return true;
    }



}