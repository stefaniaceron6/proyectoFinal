package com.example.proyectofinal.ui.productos;

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
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.proyectofinal.MySingleton;
import com.example.proyectofinal.R;
import com.example.proyectofinal.databinding.FragmentProductosBinding;
import com.example.proyectofinal.dto_categorias;
import com.example.proyectofinal.listadoProductos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductosFragment extends Fragment{

    private ProductosViewModel productosViewModel;
    private FragmentProductosBinding binding;

    private EditText id, nombre, descripcion, stock, precio, medida;
    private Spinner estado, categoria;
    private Button saved, viewP;
    int conta = 0;

    ArrayList<String> lista = null;
    ArrayList<dto_categorias> listaCategorias;
    String datoSelected = "";
    String datoSelectedC = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_productos, container, false);

        id = view.findViewById(R.id.id_pro);
        nombre = view.findViewById(R.id.nom_pro);
        descripcion = view.findViewById(R.id.des_pro);
        stock = view.findViewById(R.id.stock_pro);
        precio = view.findViewById(R.id.pre_pro);
        medida =  view.findViewById(R.id.med_pro);
        estado =  view.findViewById(R.id.estado_pro);
        categoria =  view.findViewById(R.id.list_cat);
        saved = view.findViewById(R.id.saveP);
        viewP = view.findViewById(R.id.viewP);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.estadoProductos, R.layout.support_simple_spinner_dropdown_item);
        estado.setAdapter(adapter);


        fk_categorias(getContext());



        estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (estado.getSelectedItemPosition() > 0) {
                    datoSelected = estado.getSelectedItem().toString();
                } else {
                    datoSelected = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (conta >= 1 && categoria.getSelectedItemPosition() > 0) {
                    String item_spinner = categoria.getSelectedItem().toString();

                    String s[] = item_spinner.split("-");

                    datoSelectedC = s[0].trim();

                } else {
                    datoSelectedC = "";
                }
                conta++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = id.getText().toString();
                String Nombre = nombre.getText().toString();
                String Descripcion = descripcion.getText().toString();
                String Stock = stock.getText().toString();
                String Precio = precio.getText().toString();
                String Medida = medida.getText().toString();
                String Estado = datoSelected;
                String Categoria = datoSelectedC;

                if (validarDatos(code, Nombre, Descripcion, Stock, Precio, Medida)) { // Funcion que retorna true si hay datos ingresados
                    if (estado.getSelectedItemPosition() > 0  && categoria.getSelectedItemPosition() > 0) {

                        // Metodo que guarda  la informacion en la base de datos
                        saveProductos(getContext(),code,Nombre,Descripcion,Stock,Precio,Medida,Estado,Categoria);

                    }
                }

            }
        });


        viewP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), listadoProductos.class);
                startActivity(intent);
            }
        });


        return view;
    }


    private void saveProductos (final Context context, String id, String nombre, String descripcion, String Stock, String Precio, String medida, String estado, String categoria) {
        String url = "https://coarser-coughs.000webhostapp.com/guardarProducto.php";

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
                Toast.makeText(context, "Error en la conexion" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  map = new HashMap<>();
                map.put("Content-Type", "application/json; charset=utf-8");
                map.put("Accept", "application/json");
                map.put("id_prod", String.valueOf(id));
                map.put("nombre_prod", nombre);
                map.put("desc_prod", descripcion);
                map.put("stock", Stock);
                map.put("precio_prod", Precio);
                map.put("med_prod", medida);
                map.put("est_prod", String.valueOf(estado));
                map.put("categoria", String.valueOf(categoria));
                return map;
            }

        };
        MySingleton.getInstance(context).addToRequestQueue(request);


    }





    public void fk_categorias(final Context context) {

        listaCategorias = new ArrayList<dto_categorias>();
        lista = new ArrayList<String>();
        lista.add("Seleccione Categoria");
        String url = "https://coarser-coughs.000webhostapp.com/buscarCategorias.php";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray array = new JSONArray(response);

                    int totalEncontrados = array.length();

                    dto_categorias objCategorias = null;

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject categoriasObject = array.getJSONObject(i);
                        int id_categoria = Integer.parseInt(categoriasObject.getString("id"));
                        String nombre_categoria = categoriasObject.getString("nombre");
                        int estado_categoria = Integer.parseInt(categoriasObject.getString("estado"));

                        objCategorias = new dto_categorias(id_categoria, nombre_categoria, estado_categoria);

                        listaCategorias.add(objCategorias);

                        lista.add(listaCategorias.get(i).getId()+"-"+listaCategorias.get(i).getNombre());

                        //lista.add(id_categoria,nombre_categoria);


                        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item, lista);
                        // adaptador.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        categoria.setAdapter(adaptador);


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "ERROR EN LA CONEXION DE INTERNET", Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(context).addToRequestQueue(request);
    }




    public boolean validarDatos(String code, String name, String descrip, String strock, String prec, String medid) {

        if (code.length() == 0) {
            id.setError("Ingrese ID");

        }else if( name.length() == 0) {
            nombre.setError("Ingrese Nombre");

        }else if(descrip.length() == 0){
            descripcion.setError("Ingrese Descripcion");

        } else if (strock.length() == 0) {
            stock.setError("Ingrese Stock");

        } else if (prec.length() == 0) {
            precio.setError("Ingrese Precio");

        } else if (medid.length() == 0) {
            medida.setError("Ingrese UM");

        } else if (categoria.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Debe de seleccionar una categoria", Toast.LENGTH_SHORT).show();

        } else if (estado.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Debe seleccionar un estado para el producto", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


}