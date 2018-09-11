package com.service.iscon.vcr.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by vinod on 10/4/2015.
 */
public abstract class AsyncProcess extends AsyncTask<Object, Object, Object> {

    ProgressDialog _ProgressDialog;
    Context _Context;
    String _ProgressMessage;
    boolean _ShowProgress = true;
    boolean _ProgressCancelable = true;

    public boolean is_ProgressCancelable() {
        return _ProgressCancelable;
    }

    public void set_ProgressCancelable(boolean _ProgressCancelable) {
        this._ProgressCancelable = _ProgressCancelable;
    }

    public boolean is_ShowProgress() {
        return _ShowProgress;
    }
    public void set_ShowProgress(boolean _ShowProgress) {
        this._ShowProgress = _ShowProgress;
    }


    public AsyncProcess(Context Ctx, String PrgMsg){
        _Context = Ctx;
        if(is_ShowProgress()){
            _ProgressMessage = PrgMsg;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            if (is_ShowProgress()) {
                if (_ProgressDialog == null) {
                    _ProgressDialog = new ProgressDialog(_Context);
                    _ProgressDialog.setMessage(_ProgressMessage);
                    _ProgressDialog.setIndeterminate(true);
                    if(!_ShowProgress) {
                        _ProgressDialog.setCancelable(false);
                    }
                }

                _ProgressDialog.show();

            }
        }catch (Exception e){
            Log.e("Exception", e.getMessage().toString());
            Toast.makeText(_Context,"AsycProcess Exception: "+ e.getMessage(),Toast.LENGTH_SHORT );
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if(is_ShowProgress()){
            if(_ProgressDialog != null){
                if(_ProgressDialog.isShowing())
                _ProgressDialog.dismiss();
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(is_ShowProgress()){
            if(_ProgressDialog != null){
                _ProgressDialog.dismiss();
            }
        }
    }
}