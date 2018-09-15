package ts.utill.customermanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import ts.utill.customermanager.data.CustomerDB;

public class CSVEditActivity extends AppCompatActivity {

	String filename = null;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_csvedit);
		
		EditText editText_CsvEdit = (EditText)findViewById(R.id.editText_CsvEdit);
		
		Intent intent  = getIntent();
		filename = intent.getStringExtra("filename");
		
		if(filename != null){
			try {
				File directory = new File(CustomerDB.filepath);
				if (!directory.exists()) {
					directory.mkdirs();
				} 
				
				File file = new File(CustomerDB.filepath + filename);
				file.createNewFile();
				InputStream in = new FileInputStream(file);
				
				byte[] b = new byte[(int) file.length()];
				int x = in.read(b);
				
				editText_CsvEdit.setText(new String(b).toString());
				
				
			} catch (Throwable t) {
				Toast.makeText(this, "Err " + t.toString(), Toast.LENGTH_LONG).show();
				Log.d("TestG", "Err " + t.toString());
			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.csvedit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_chek) {
			
			new AlertDialog.Builder(CSVEditActivity.this)
			.setTitle(R.string.warning)
			.setIcon(R.mipmap.warning)
			.setMessage(R.string.resetsave_Q)
			.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
					CSVSave(filename);
					
					//Finish
					finish();
				}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void CSVSave(String filename) {
		try {
			//OutputStreamWriter out = new OutputStreamWriter(openFileOutput(filename +  ".csv", 0));
			File directory = new File(CustomerDB.filepath);
			if (!directory.exists()) {
				directory.mkdirs();
			} 
			
			File fileCacheItem = new File(CustomerDB.filepath + filename);
			fileCacheItem.createNewFile();

			
			OutputStream out = new FileOutputStream(fileCacheItem);
			
			EditText editText_CsvEdit = (EditText)findViewById(R.id.editText_CsvEdit);
			
			String buf = editText_CsvEdit.getText().toString();
			
			out.write(buf.toString().getBytes());			
			out.close();
			
			Toast.makeText(this, "Save " + CustomerDB.filepath + filename, Toast.LENGTH_LONG).show();
		} catch (Throwable t) {
			Toast.makeText(this, "Err " + t.toString(), Toast.LENGTH_LONG).show();
			Log.d("TestG", "Err " + t.toString());
		}
	}
	
}
