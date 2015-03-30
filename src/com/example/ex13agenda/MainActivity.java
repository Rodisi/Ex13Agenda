package com.example.ex13agenda;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	
	private Cursor cursor;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String[] campos = new String[]
		{ ContactsContract.Contacts.DISPLAY_NAME ,ContactsContract.Contacts.HAS_PHONE_NUMBER};
		String selection = "(" + ContactsContract.Contacts.IN_VISIBLE_GROUP + 
				" = '1' AND (" + ContactsContract.Contacts.HAS_PHONE_NUMBER + " != 0 ))";
		int[] views = new int[] { android.R.id.text1 };
		cursor = getContentResolver().query(
		ContactsContract.Contacts.CONTENT_URI,
		null, selection, null, null);
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
		MainActivity.this, // Contexto
		android.R.layout.simple_list_item_1, // Vista item
		cursor, // Cursor com dados
		campos, // Projeção de campos
		views // Projeção de vistas
		);
		setListAdapter(adapter);
		
		
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		final EditText textoSms = (EditText) findViewById(R.id.editText1);
		final String sms = textoSms.getText().toString();
		Cursor tlfCur = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				// Url de pesquisa
				null, // projeção de campos (retiramos todos)
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", // Condição
				new String[] { "" + id }, // Campos para a condição
				null); // Ordem
		// Gerar a lista de telefones para criar um diálogo
		int nTelefones = tlfCur.getCount();
		final String[] telefones = new String[nTelefones];
		
		int x = 0;
		while (tlfCur.moveToNext()) {
		int col = tlfCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
		telefones[x++] = tlfCur.getString(col);
		}
		tlfCur.close();
		// Criamos o diálogo
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Seleciona o telefone");
		builder.setItems(telefones, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int item) {
			
			
			try {
				// O envio propriamente dito
				SmsManager smsMgr = SmsManager.getDefault();
				smsMgr.sendTextMessage(telefones[item], null, sms, null, null);
				// Avisamos o utilizador
				Toast.makeText(MainActivity.this, "SMS enviado",
				Toast.LENGTH_LONG).show();
				} catch (Exception e) {
				Toast.makeText(MainActivity.this, "Erro no envio: " + e.getLocalizedMessage(),
				Toast.LENGTH_SHORT).show();
				}
			
			
		}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
}
