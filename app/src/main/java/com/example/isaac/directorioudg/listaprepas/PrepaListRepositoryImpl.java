package com.example.isaac.directorioudg.listaprepas;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.isaac.directorioudg.R;
import com.example.isaac.directorioudg.db.DirectorioDataBase;
import com.example.isaac.directorioudg.entities.Prepa;
import com.example.isaac.directorioudg.entities.Prepa_Table;
import com.example.isaac.directorioudg.listaprepas.adapters.PrepasAdapter;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.List;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class PrepaListRepositoryImpl{

    Context context;
    PrepasAdapter adapter=null;
    public PrepaListRepositoryImpl() {
        this.context = getContext().getApplicationContext();
    }

    public PrepaListRepositoryImpl( PrepasAdapter adapter) {
        this.context = getContext().getApplicationContext();
        this.adapter=adapter;
    }


    public void descargarDatosPrepa(String url) {
        try {

            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    parsearDatosPrepaDBFlow(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Error al cargar datos", Toast.LENGTH_LONG).show();
                }
            });
            Volley.newRequestQueue(context).add(request);

        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void descargarDatosPrepaCompletos(PrepasAdapter adapteraux) {
        String ruta= getContext().getResources().getString(R.string.prefijoWebService)+"preparatorias.php";
        descargarDatosPrepa(ruta);
    }

    public void descargarDatosPrepaCompletos() {
        String ruta= getContext().getResources().getString(R.string.prefijoWebService)+"preparatorias.php";
        descargarDatosPrepa(ruta);
    }

    public void parsearDatosPrepaDBFlow(String json) {
        //noinspection EmptyCatchBlock
        try {

            Object objetoJson = JSONValue.parse(json);
            JSONArray jsonArrayObject = (JSONArray) objetoJson;
            ArrayList<Prepa> listPrepas = new ArrayList();
            for (int i = 0; i < jsonArrayObject.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArrayObject.get(i);
                //Crea sentencias sql para agregar a una lista
                Prepa prepa = new Prepa();
                prepa.setIdPrepa( Integer.parseInt(jsonObject.get("idPrepa").toString()));
                prepa.setPreparatoria(jsonObject.get("Preparatoria").toString());
                prepa.setMetropolitana(Integer.parseInt(jsonObject.get("Metropolitana").toString()));
                prepa.setDireccion(jsonObject.get("Direccion").toString());
                prepa.setMunicipio(jsonObject.get("Municipio").toString());
                prepa.setCP(Integer.parseInt(jsonObject.get("CP").toString()));
                prepa.setTelefono1(jsonObject.get("Telefono1").toString());
                prepa.setTelefono2(jsonObject.get("Telefono2").toString());
                prepa.setWEB(jsonObject.get("Web").toString());
                prepa.setLatitud(Double.parseDouble(jsonObject.get("Latitud").toString()));
                prepa.setLongitud(Double.parseDouble(jsonObject.get("Longitud").toString()));
                prepa.setImagenURL(jsonObject.get("imagenURL").toString());

                prepa.setDirector(jsonObject.get("Director").toString());
                prepa.setFotoDirectorURL(jsonObject.get("FotoDirectorURL").toString());
                prepa.setCorreoDirector(jsonObject.get("CorreoDirector").toString());
                prepa.setSecretario(jsonObject.get("Secretario").toString());
                prepa.setCorreoSecretario(jsonObject.get("CorreoSecretario").toString());

                listPrepas.add(prepa);
            }

            FlowManager.getDatabase(DirectorioDataBase.class)
                    .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                            new ProcessModelTransaction.ProcessModel<Prepa>() {
                                @Override
                                public void processModel(Prepa prepa) {
                                    // do work here -- i.e. user.delete() or user.update()
                                    prepa.save();

                                }
                            }).addAll(listPrepas).build())  // add elements (can also handle multiple)
                    .error(new Transaction.Error() {
                        @Override
                        public void onError(Transaction transaction, Throwable error) {
                        }
                    })
                    .success(new Transaction.Success() {
                        @Override
                        public void onSuccess(Transaction transaction) {
                            if(adapter!=null){
                                adapter.setPrepaList(getListPrepas(0));
                            }
                        }
                    }).build().execute();

        } catch (SQLiteException e) {
        }
    }


    public List<Prepa> getListPrepas(int filter) {
        List<Prepa> prepaList;
        if(filter==0){
             prepaList = new Select().from(Prepa.class).queryList();
        }else if (filter==1){
             prepaList = new Select().from(Prepa.class).where(Prepa_Table.Metropolitana.is(1)).queryList();
        }
        else {
            prepaList = new Select().from(Prepa.class).where(Prepa_Table.Metropolitana.is(0)).queryList();
        }
        return prepaList;
    }


    public Prepa getPrepa(int id) {
        return new Select().from(Prepa.class).where(Prepa_Table.idPrepa.is(id)).querySingle();
    }

}
