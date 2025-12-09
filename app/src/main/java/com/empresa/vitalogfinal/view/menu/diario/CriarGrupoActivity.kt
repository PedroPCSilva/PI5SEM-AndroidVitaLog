package com.empresa.vitalogfinal.view.menu.diario

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.empresa.vitalogfinal.R

class CriarGrupoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_grupo)

        // CORREÇÃO: O ID no XML é 'edit_nome_grupo', não 'edt_'
        val editNome = findViewById<EditText>(R.id.edit_nome_grupo)
        val buttonCriar = findViewById<Button>(R.id.button_criar_grupo)
        val btnCancelar = findViewById<Button>(R.id.button_cancelar_grupo)

        btnCancelar.setOnClickListener {
            finish()
        }

        buttonCriar.setOnClickListener {
            val nome = editNome.text.toString().trim()
            if (nome.isEmpty()) {
                Toast.makeText(this, "Digite um nome para o grupo", Toast.LENGTH_SHORT).show()
            } else {
                val resultIntent = Intent()
                resultIntent.putExtra("nome_grupo", nome)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}