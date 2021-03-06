package com.example.isaac.directorioudg.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.isaac.directorioudg.db.DirectorioDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

/**
 * Created by Usuario on 10/10/2016.
 */
@Table( database= DirectorioDataBase.class)
public class ContenidoGaceta extends BaseModel implements Parcelable {

    public ContenidoGaceta() {
    }

    @Column
    @PrimaryKey
    private int id;
    @Column private int anyo;
    @Column private int mes;
    @Column private int dia;
    @Column private String urlContenido;
    @Column private String urlImage;

    public int getAnyo() {
        return anyo;
    }

    public void setAnyo(int anyo) {
        this.anyo = anyo;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrlContenido() {
        return urlContenido;
    }

    public void setUrlContenido(String urlContenido) {
        this.urlContenido = urlContenido;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.anyo);
        dest.writeInt(this.mes);
        dest.writeInt(this.dia);
        dest.writeString(this.urlContenido);
        dest.writeString(this.urlImage);
    }

    protected ContenidoGaceta(Parcel in) {
        this.id = in.readInt();
        this.anyo = in.readInt();
        this.mes = in.readInt();
        this.dia = in.readInt();
        this.urlContenido = in.readString();
        this.urlImage = in.readString();
    }

    public static final Parcelable.Creator<ContenidoGaceta> CREATOR = new Parcelable.Creator<ContenidoGaceta>() {
        @Override
        public ContenidoGaceta createFromParcel(Parcel source) {
            return new ContenidoGaceta(source);
        }

        @Override
        public ContenidoGaceta[] newArray(int size) {
            return new ContenidoGaceta[size];
        }
    };
}
