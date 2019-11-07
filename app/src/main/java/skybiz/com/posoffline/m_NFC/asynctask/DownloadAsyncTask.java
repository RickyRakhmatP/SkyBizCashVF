package skybiz.com.posoffline.m_NFC.asynctask;

import android.os.AsyncTask;


import skybiz.com.posoffline.m_NFC.interfaces.IAsyncTask;

public class DownloadAsyncTask extends AsyncTask<Void, Void, Object> {
	IAsyncTask asyncTask;
	
	public DownloadAsyncTask(IAsyncTask _asyncTask) {
		this.asyncTask = _asyncTask;
	}
	
	@Override
	protected void onPreExecute() {
		if(this.asyncTask != null)
			asyncTask.showProgressbar();
	}

	@Override
	protected Object doInBackground(Void... params) {
		try{
			if(this.asyncTask != null)
				return this.asyncTask.doInBackground();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Object data) {
		if(this.asyncTask != null){
			this.asyncTask.hideProgressbar();
			this.asyncTask.precessDataAfterDownlaod(data);
		}
	}

}
