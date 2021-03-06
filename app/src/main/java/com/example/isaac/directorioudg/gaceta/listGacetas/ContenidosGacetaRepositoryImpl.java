package com.example.isaac.directorioudg.gaceta.listGacetas;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.isaac.directorioudg.R;
import com.example.isaac.directorioudg.db.DirectorioDataBase;
import com.example.isaac.directorioudg.entities.ContenidoGaceta;
import com.example.isaac.directorioudg.entities.ContenidoGaceta_Table;
import com.example.isaac.directorioudg.gaceta.listGacetas.adapters.GacetasAdapter;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

/**
 * Created by Isaac on 10/10/2016.
 */

public class ContenidosGacetaRepositoryImpl{
    Context context;
    Boolean adapterisEmpty=true;
    GacetasAdapter adapter=null;


    public ContenidosGacetaRepositoryImpl() {
        this.context = getContext().getApplicationContext();

    }
    public ContenidosGacetaRepositoryImpl(GacetasAdapter adapter) {
        this.context = getContext().getApplicationContext();
        this.adapter=adapter;
    }


    private void descargarDatosContenidoGaceta(String url) {
        try {

            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    parsearDatosDBFlow(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            Volley.newRequestQueue(context).add(request);

        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }


    public void descargarDatosContenidoGaceta(GacetasAdapter adapteraux) {
        adapterisEmpty=false;
        adapter=adapteraux;
        String ruta= getContext().getResources().getString(R.string.prefijoWebService)+"contenidosGaceta.php";
        descargarDatosContenidoGaceta(ruta);
    }


    public void descargarDatosContenidoGacetaCompletos() {
        adapterisEmpty=true;
        String ruta= getContext().getResources().getString(R.string.prefijoWebService)+"contenidosGaceta.php";
        descargarDatosContenidoGaceta(ruta);
    }


    private void parsearDatosDBFlow(String json) {
        try {

            Object objetoJson = JSONValue.parse(json);
            JSONArray jsonArrayObject = (JSONArray) objetoJson;
            final ArrayList<ContenidoGaceta> list = new ArrayList();
            for (int i = 0; i < jsonArrayObject.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArrayObject.get(i);
                //Crea sentencias sql para agregar a una lista
                ContenidoGaceta contenidoGaceta = new ContenidoGaceta();
                contenidoGaceta.setId( Integer.parseInt(jsonObject.get("id").toString()));

                String fecha = jsonObject.get("fecha").toString();
                StringTokenizer tokens=new StringTokenizer(fecha, "-");
                String anyo=tokens.nextToken();
                String mes=tokens.nextToken();
                String dia=tokens.nextToken();
                contenidoGaceta.setAnyo(Integer.parseInt(anyo));
                contenidoGaceta.setMes(Integer.parseInt(mes));
                contenidoGaceta.setDia(Integer.parseInt(dia));

                contenidoGaceta.setUrlContenido(jsonObject.get("urlContenido").toString());
                contenidoGaceta.setUrlImage(jsonObject.get("urlImage").toString());
                list.add(contenidoGaceta);
            }

            FlowManager.getDatabase(DirectorioDataBase.class)
                    .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                            new ProcessModelTransaction.ProcessModel<ContenidoGaceta>() {
                                @Override
                                public void processModel(ContenidoGaceta contenidoGaceta) {
                                    // do work here -- i.e. user.delete() or user.update()
                                    contenidoGaceta.save();
                                }
                            }).addAll(list).build())  // add elements (can also handle multiple)
                    .error(new Transaction.Error() {
                        @Override
                        public void onError(Transaction transaction, Throwable error) {

                        }
                    })
                    .success(new Transaction.Success() {
                        @Override
                        public void onSuccess(Transaction transaction) {
                            if(!adapterisEmpty){
                                adapter.setList(list);
                            }
                        }
                    }).build().execute();

        } catch (SQLiteException e) {
            Log.e("llenarBaseDatos: ", e.getMessage());
        }
    }

   public List<ContenidoGaceta> getList() {
        List<ContenidoGaceta> List;
            List = new Select().from(ContenidoGaceta.class).where(ContenidoGaceta_Table.id.lessThan(10)).orderBy(ContenidoGaceta_Table.id,false).queryList();
        return List;
    }

    public ContenidoGaceta getPorId(int id) {
        ContenidoGaceta contenidoGaceta = new Select().from(ContenidoGaceta.class).where(ContenidoGaceta_Table.id.is(id)).querySingle();
        return contenidoGaceta;
    }

    public void getPorFecha(int anyo, int mes) {
        List<ContenidoGaceta> List;
        List = new Select().from(ContenidoGaceta.class)
                .where(ContenidoGaceta_Table.anyo.is(anyo))
                .and(ContenidoGaceta_Table.mes.is(mes))
                .orderBy(ContenidoGaceta_Table.id,false).queryList();
        adapter.setList(List);
    }

}
