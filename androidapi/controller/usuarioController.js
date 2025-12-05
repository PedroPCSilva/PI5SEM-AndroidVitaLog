const { con } = require('../connection');

// Wrapper para usar Promises com MySQL (Permite usar await)
function queryPromise(sql) {
    return new Promise((resolve, reject) => {
        con.query(sql, (err, result) => {
            if (err) return reject(err);
            resolve(result);
        });
    });
}

// ========================
// LOGIN
// ========================
async function login(data) {
    console.log("Tentando logar...");
    let sql = `SELECT * FROM tb_usuarios WHERE email='${data.email}' AND senha='${data.senha}'`;
    return queryPromise(sql);
}

// ========================
// CADASTRO
// ========================
async function cadastro(data) {
    console.log("Tentando cadastrar...");
    let sql = `INSERT INTO tb_usuarios (nome, email, senha) VALUES ('${data.nome}', '${data.email}', '${data.senha}')`;
    return queryPromise(sql);
}

// ========================
// BUSCAR PERFIL
// ========================
async function buscarPorId(id) {
    let sql = `SELECT * FROM tb_usuarios WHERE id = ${id}`;
    return queryPromise(sql);
}

// ========================
// ATUALIZAR DADOS CADASTRAIS
// ========================
async function atualizarDados(id, data) {
    // 1. Verifica se a senha informada bate com a do banco
    // (É uma medida de segurança para ninguém alterar dados se o celular estiver desbloqueado na mão de outra pessoa)
    let checkPass = `SELECT id FROM tb_usuarios WHERE id = ${id} AND senha = '${data.senha_atual}'`;
    let userCheck = await queryPromise(checkPass);

    if (userCheck.length === 0) {
        throw new Error("Senha incorreta");
    }

    // 2. Se a senha estiver certa, atualiza os dados
    let sql = `
        UPDATE tb_usuarios 
        SET nome = '${data.nome}',
            email = '${data.email}',
            sobrenome = '${data.sobrenome}',
            data_nascimento = '${data.data_nascimento}',
            peso = ${data.peso}, 
            altura = ${data.altura}
        WHERE id = ${id}
    `;
    return queryPromise(sql);
}

// ========================
// ALTERAR SENHA
// ========================
async function alterarSenha(id, data) {
    // data espera: { senha_atual, nova_senha }
    
    // 1. Verifica a senha antiga
    let checkPass = `SELECT id FROM tb_usuarios WHERE id = ${id} AND senha = '${data.senha_atual}'`;
    let userCheck = await queryPromise(checkPass);

    if (userCheck.length === 0) {
        throw new Error("Senha atual incorreta");
    }

    // 2. Atualiza para a nova senha
    let sqlUpdate = `UPDATE tb_usuarios SET senha = '${data.nova_senha}' WHERE id = ${id}`;
    return queryPromise(sqlUpdate);
}

module.exports = {
    login,
    cadastro,
    buscarPorId,
    atualizarDados,
    alterarSenha
};