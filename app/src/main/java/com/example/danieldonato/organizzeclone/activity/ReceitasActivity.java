package com.example.danieldonato.organizzeclone.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.danieldonato.organizzeclone.R;
import com.example.danieldonato.organizzeclone.config.ConfiguracaoFirebase;
import com.example.danieldonato.organizzeclone.helper.Base64Custom;
import com.example.danieldonato.organizzeclone.helper.DateCustom;
import com.example.danieldonato.organizzeclone.model.Movimentacao;
import com.example.danieldonato.organizzeclone.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitasActivity extends AppCompatActivity {

    private TextInputEditText txtData, txtCategoria, txtDescricao;
    private EditText txtValor;
    private FloatingActionButton fabSalvar;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double receitaTotal;
    private Double receitaAtualizada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        txtData = findViewById(R.id.txtDataReceita);
        txtCategoria = findViewById(R.id.txtCategoriaReceita);
        txtDescricao = findViewById(R.id.txtDescricaoReceita);
        txtValor = findViewById(R.id.txtValorReceita);
        fabSalvar = findViewById(R.id.fabSalvarReceita);

        txtData.setText(DateCustom.dataAtual());
        this.recuperarReceitaTotal();

        fabSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarReceita();
            }
        });

    }

    public void salvarReceita(){
        if(validarCamposReceita()){
            Double valorRecuperado = Double.parseDouble(txtValor.getText().toString());
            movimentacao = new Movimentacao();
            movimentacao.setValor(valorRecuperado);
            movimentacao.setCategoria(txtCategoria.getText().toString());
            movimentacao.setDescricao(txtDescricao.getText().toString());
            movimentacao.setDataMovimentacao(txtData.getText().toString());
            movimentacao.setTipo("r");
            String data = movimentacao.getDataMovimentacao();
            receitaAtualizada = receitaTotal + valorRecuperado;
            atualizarReceita(receitaAtualizada);

            movimentacao.salvar(data);

            finish();

        }
    }

    public Boolean validarCamposReceita(){

        String valor = txtValor.getText().toString();
        String data = txtData.getText().toString();
        String descricao = txtDescricao.getText().toString();
        String categoria = txtCategoria.getText().toString();

        if(!valor.isEmpty()){
            if(!data.isEmpty()){
                if(!categoria.isEmpty()){
                    if(!descricao.isEmpty()){
                        return true;
                    }else{
                        Toast.makeText(ReceitasActivity.this,
                                "Preencha o descricao",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(ReceitasActivity.this,
                            "Preencha o categoria",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(ReceitasActivity.this,
                        "Preencha o data",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(ReceitasActivity.this,
                    "Preencha o valor",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void recuperarReceitaTotal(){
        String email = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(email);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void atualizarReceita(Double receita){
        String email = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(email);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.child("receitaTotal").setValue(receita);

    }

}
