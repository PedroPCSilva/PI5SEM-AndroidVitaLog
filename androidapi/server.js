const express = require('express');
const nodemailer = require('nodemailer'); 
// const bcrypt = require('bcrypt'); // <-- REMOVIDO PARA FUNCIONAR COM SEU LOGIN ATUAL
const { con } = require('./connection'); 

// Função para permitir usar 'await' no banco de dados
function queryPromise(sql) {
    return new Promise((resolve, reject) => {
        con.query(sql, (err, result) => {
            if (err) return reject(err);
            resolve(result);
        });
    });
}

// Importação dos Controllers
const usuario = require('./controller/usuarioController');
const diario = require('./controller/diarioController');
const alimento = require('./controller/alimentoController');
const hidratacao = require('./controller/hidratacaoController');
const meta = require('./controller/metaController');
const relatorio = require('./controller/relatorioController');

const app = express();
const port = 8066;

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// =========================
// CONFIGURAÇÃO DO EMAIL (GMAIL)
// =========================
const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: 'pis.sen4c.bs1@gmail.com', 
        pass: 'rcptxxpdvlnwzzkc' 
    }
});

// =========================
// ROTA ESQUECI A SENHA (AJUSTADA PARA LOGIN SEM CRIPTOGRAFIA)
// =========================
app.post('/api/esqueci-senha', async (req, res) => {
    const { email } = req.body;
    console.log(`[RESET] Tentativa para: ${email}`);

    if (!email) return res.status(400).json({ message: "E-mail obrigatório" });

    try {
        // 1. Busca na tabela tb_usuarios
        const sqlBusca = `SELECT * FROM tb_usuarios WHERE email = '${email}'`;
        const rows = await queryPromise(sqlBusca);

        if (!rows || rows.length === 0) {
            console.log("❌ Email não encontrado na tabela tb_usuarios.");
            return res.status(404).json({ message: "E-mail não cadastrado." });
        }

        console.log("✅ Usuário encontrado! Gerando nova senha...");

        // 2. Gera senha temporária
        const senhaTemporaria = Math.random().toString(36).slice(-8);

        // --- MUDANÇA AQUI: SALVAR SENHA PURA ---
        // Para usar criptografia (bcrypt), você teria que reescrever o usuarioController.js inteiro.
        // Como não temos tempo, vamos salvar a senha pura para o login funcionar agora.
        
        // 3. Atualiza na tabela tb_usuarios (SEM CRIPTOGRAFIA)
        const sqlUpdate = `UPDATE tb_usuarios SET senha = '${senhaTemporaria}' WHERE email = '${email}'`;
        await queryPromise(sqlUpdate);

        // 4. Envia E-mail
        const mailOptions = {
            from: 'Suporte Vitalog <pis.sen4c.bs1@gmail.com>',
            to: email,
            subject: 'Recuperação de Acesso - Vitalog',
            html: `
                <div style="font-family: Arial, sans-serif; color: #333;">
                    <h2>Vitalog - Recuperação de Senha</h2>
                    <p>Sua nova senha temporária é:</p>
                    <h1 style="color: #11C5C2; letter-spacing: 2px;">${senhaTemporaria}</h1>
                    <p>Use esta senha para entrar no aplicativo.</p>
                </div>
            `
        };

        await transporter.sendMail(mailOptions);
        console.log(`[SUCESSO] Email enviado para ${email}`);
        
        res.json({ success: true, message: "Nova senha enviada por e-mail!" });

    } catch (error) {
        console.error("[ERRO NO SERVIDOR]", error);
        res.status(500).json({ message: "Erro interno ao resetar senha." });
    }
});

// =========================
// USUÁRIO - LOGIN, CADASTRO, PERFIL
// =========================
app.get('/usuario/login', async (req, res) => {
    try {
        const resultSet = await usuario.login(req.query);
        if (resultSet.length > 0) {
            return res.status(200).send({
                message: "Login bem-sucedido!",
                user: resultSet[0]
            });
        } else {
            return res.status(401).send({ message: "Credenciais inválidas." });
        }
    } catch (err) {
        return res.status(500).send({ message: "Erro no login." });
    }
});

app.post('/usuario/cadastro', async (req, res) => {
    try {
        const result = await usuario.cadastro(req.body);
        if (result.affectedRows > 0) {
            return res.status(201).send({
                message: "Usuário cadastrado com sucesso!",
                id: result.insertId
            });
        }
    } catch (err) {
        return res.status(500).send({ message: "Erro ao cadastrar usuário." });
    }
});

app.get('/usuario/:id', async (req, res) => {
    try {
        const result = await usuario.buscarPorId(req.params.id);
        if (result.length > 0) {
            res.status(200).send(result[0]);
        } else {
            res.status(404).send({ message: "Usuário não encontrado." });
        }
    } catch (err) {
        console.error(err);
        res.status(500).send({ message: "Erro ao buscar perfil." });
    }
});

app.put('/usuario/:id', async (req, res) => {
    try {
        await usuario.atualizarDados(req.params.id, req.body);
        res.status(200).send({ message: "Dados atualizados!" });
    } catch (err) {
        if (err.message === "Senha incorreta") {
            return res.status(401).send({ message: "Senha atual incorreta." });
        }
        console.error(err);
        res.status(500).send({ message: "Erro ao atualizar perfil." });
    }
});

app.patch('/usuario/senha/:id', async (req, res) => {
    try {
        await usuario.alterarSenha(req.params.id, req.body);
        res.status(200).send({ message: "Senha alterada com sucesso!" });
    } catch (err) {
        if (err.message === "Senha atual incorreta") {
            return res.status(401).send({ message: "Senha atual incorreta." });
        }
        res.status(500).send({ message: "Erro ao alterar senha." });
    }
});

// =========================
// RELATÓRIOS
// =========================
app.get('/relatorio/semanal/:id', async (req, res) => {
    try {
        const result = await relatorio.getSemanal(req.params.id);
        res.status(200).send(result);
    } catch (err) {
        console.error(err);
        res.status(500).send({ message: "Erro ao gerar relatório." });
    }
});

// ========================
// DIARIO & ALIMENTOS
// ========================
app.get('/diario/grupos', async (req, res) => {
    try {
        const result = await diario.listarGrupos(req.query);
        return res.status(200).send(result);
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.get('/diario/grupo/alimentos', async (req, res) => {
    try {
        const result = await diario.listarAlimentos(req.query.grupo_id);
        return res.status(200).send(result);
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.post('/diario/grupo', async (req, res) => {
    try {
        const result = await diario.criarGrupo(req.body);
        return res.status(201).send({ message: "Grupo criado!", id: result.insertId });
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.delete('/diario/grupo/:id', async (req, res) => {
    try {
        await diario.apagarGrupo(req.params.id);
        return res.status(200).send({ message: "Grupo apagado!" });
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.get('/diario/:usuarioId/:data', async (req, res) => {
    try {
        const result = await diario.listarGrupos({ usuario_id: req.params.usuarioId, data: req.params.data });
        return res.status(200).send(result);
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.post('/diario/:usuarioId/grupo', async (req, res) => {
    try {
        const result = await diario.criarGrupo({ usuario_id: req.params.usuarioId, nome: req.body.nome });
        return res.status(201).send(result);
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.get('/grupo/:id', async (req, res) => {
    try {
        const result = await diario.buscarGrupoPorId(req.params.id);
        if (result.length > 0) return res.status(200).send(result[0]);
        else return res.status(404).send({ message: "Grupo não encontrado." });
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.get('/diario/total/:usuarioId/:data', async (req, res) => {
    try {
        const result = await diario.obterTotalCalorias(req.params.usuarioId, req.params.data);
        const total = result[0].total || 0;
        return res.status(200).send({ total: total });
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.post('/alimento', async (req, res) => {
    try {
        const result = await alimento.adicionarAlimento(req.body);
        return res.status(201).send({ message: "Alimento inserido!", id: result.insertId });
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.put('/alimento/:id', async (req, res) => {
    try {
        await alimento.editarAlimento(req.params.id, req.body);
        return res.status(200).send({ message: "Alimento atualizado!" });
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.delete('/alimento/:id', async (req, res) => {
    try {
        await alimento.apagarAlimento(req.params.id);
        return res.status(200).send({ message: "Alimento apagado!" });
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.get('/diario/grupo/:id/alimentos', async (req, res) => {
    try {
        const result = await diario.listarAlimentos(req.params.id);
        return res.status(200).send(result);
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.get('/alimentos/pesquisa', async (req, res) => {
    try {
        const result = await alimento.buscarAlimentosBase(req.query.q);
        return res.status(200).send(result);
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

// ========================
// HIDRATAÇÃO & METAS
// ========================
app.get('/hidratacao/:usuarioId/:data', async (req, res) => {
    try {
        const result = await hidratacao.listar(req.params);
        return res.status(200).send(result);
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.post('/hidratacao', async (req, res) => {
    try {
        const result = await hidratacao.adicionar(req.body);
        return res.status(201).send({ message: "Hidratação salva!", id: result.insertId });
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.delete('/hidratacao/:id', async (req, res) => {
    try {
        await hidratacao.remover(req.params.id);
        return res.status(200).send({ message: "Removido!" });
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.get('/meta/:usuarioId/:data', async (req, res) => {
    try {
        const result = await meta.listar(req.params.usuarioId, req.params.data);
        return res.status(200).send(result);
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.post('/meta', async (req, res) => {
    try {
        const result = await meta.definir(req.body);
        return res.status(200).send(result);
    } catch (err) { return res.status(500).send({ message: "Erro." }); }
});

app.listen(port, () => {
    console.log(`API rodando: http://localhost:${port}`);
});