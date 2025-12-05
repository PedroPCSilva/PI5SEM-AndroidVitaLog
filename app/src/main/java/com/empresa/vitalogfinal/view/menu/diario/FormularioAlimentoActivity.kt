package com.empresa.vitalogfinal.view.menu.diario

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.empresa.vitalogfinal.R
import com.empresa.vitalogfinal.credenciais.Credenciais
import com.empresa.vitalogfinal.model.diario.FoodModel
import com.empresa.vitalogfinal.repository.AlimentoRepository
import com.empresa.vitalogfinal.service.AlimentoService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FormularioAlimentoActivity : AppCompatActivity() {

    private lateinit var repository: AlimentoRepository

    private var usuarioId = 0
    private var grupoId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_alimento)

        usuarioId = intent.getIntExtra("usuarioId", 0)
        grupoId = intent.getIntExtra("grupoId", 0)

        val edtNome = findViewById<EditText>(R.id.edt_form_nome)
        val edtPorcaoBase = findViewById<EditText>(R.id.edt_form_porcao_base)
        val edtCaloriaBase = findViewById<EditText>(R.id.edt_form_caloria_base)
        val edtConsumido = findViewById<EditText>(R.id.edt_form_consumido)
        val txtTotal = findViewById<TextView>(R.id.txt_resultado_calculo)
        val btnSalvar = findViewById<Button>(R.id.btn_salvar_alimento)

        val nomeExtra = intent.getStringExtra("nome")
        if (nomeExtra != null) {
            edtNome.setText(nomeExtra)
            edtPorcaoBase.setText(intent.getDoubleExtra("porcao_base", 100.0).toString())
            edtCaloriaBase.setText(intent.getDoubleExtra("caloria_base", 0.0).toString())

            edtConsumido.requestFocus()
        }

        val cred = Credenciais()
        val retrofit = Retrofit.Builder()
            .baseUrl(cred.ip)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        repository = AlimentoRepository(retrofit.create(AlimentoService::class.java))

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calcularTotal(edtPorcaoBase, edtCaloriaBase, edtConsumido, txtTotal)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        edtConsumido.addTextChangedListener(textWatcher)
        edtPorcaoBase.addTextChangedListener(textWatcher)
        edtCaloriaBase.addTextChangedListener(textWatcher)

        btnSalvar.setOnClickListener {
            val nome = edtNome.text.toString()
            val pBase = edtPorcaoBase.text.toString().toDoubleOrNull() ?: 0.0
            val cBase = edtCaloriaBase.text.toString().toDoubleOrNull() ?: 0.0
            val consumido = edtConsumido.text.toString().toDoubleOrNull() ?: 0.0

            if (nome.isEmpty() || pBase == 0.0 || consumido == 0.0) {
                Toast.makeText(this, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
            } else {
                salvarNoBanco(nome, cBase, pBase, consumido)
            }
        }
    }

    private fun calcularTotal(pBase: EditText, cBase: EditText, consumido: EditText, txtResult: TextView) {
        val p = pBase.text.toString().toDoubleOrNull() ?: 0.0
        val c = cBase.text.toString().toDoubleOrNull() ?: 0.0
        val q = consumido.text.toString().toDoubleOrNull() ?: 0.0

        if (p > 0) {
            val total = (c / p) * q
            txtResult.text = String.format("Total: %.0f kcal", total)
        } else {
            txtResult.text = "Total: 0 kcal"
        }
    }

    private fun salvarNoBanco(nome: String, calBase: Double, porcaoBase: Double, consumido: Double) {
        val novoAlimento = FoodModel(
            id = 0,
            usuario_id = usuarioId,
            grupo_id = grupoId,
            nome = nome,
            caloria_base = calBase,
            porcao_consumida = consumido,
            porcao_base = porcaoBase,
            data_registro = ""
        )

        lifecycleScope.launch {
            val sucesso = repository.salvar(novoAlimento)
            if (sucesso) {
                Toast.makeText(this@FormularioAlimentoActivity, "Alimento Adicionado!", Toast.LENGTH_SHORT).show()
                finish() // Volta para a tela anterior
            } else {
                Toast.makeText(this@FormularioAlimentoActivity, "Erro ao salvar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}