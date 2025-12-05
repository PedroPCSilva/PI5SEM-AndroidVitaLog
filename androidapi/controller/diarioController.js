const { con } = require('../connection');

// Wrapper de Promises
function queryPromise(sql) {
    return new Promise((resolve, reject) => {
        con.query(sql, (err, result) => {
            if (err) return reject(err);
            resolve(result);
        });
    });
}

// ========================
// LISTAR GRUPOS DO DIA
// ========================
async function listarGrupos(data) {
    let sql = `
        SELECT * FROM grupo_alimentos_usuario
        WHERE usuario_id = ${data.usuario_id}
        AND DATE(data_registro) = '${data.data}'
        ORDER BY data_registro ASC
    `;
    return queryPromise(sql);
}

// ================================
// LISTAR ALIMENTOS DE UM GRUPO
// ================================
async function listarAlimentos(grupo_id) {
    let sql = `
        SELECT * FROM tb_alimentos_usuario
        WHERE grupo_id = ${grupo_id}
        ORDER BY data_registro ASC
    `;
    return queryPromise(sql);
}

// ================================
// CRIAR NOVO GRUPO
// ================================
async function criarGrupo(data) {
    // Insere o grupo
    let result = await queryPromise(`
        INSERT INTO grupo_alimentos_usuario (usuario_id, nome, data_registro)
        VALUES (${data.usuario_id}, '${data.nome}', NOW())
    `);

    // Busca o grupo recém-criado
    let grupoCriado = await queryPromise(`
        SELECT * FROM grupo_alimentos_usuario WHERE id = ${result.insertId}
    `);

    return grupoCriado[0]; // retorna objeto completo
}


// ================================
// APAGAR GRUPO
// ================================
async function apagarGrupo(id) {
    // Primeiro apaga alimentos do grupo
    await queryPromise(`DELETE FROM tb_alimentos_usuario WHERE grupo_id = ${id}`);

    // Depois apaga o grupo
    return queryPromise(`DELETE FROM grupo_alimentos_usuario WHERE id = ${id}`);
}

async function buscarGrupoPorId(id) {
    let sql = `SELECT * FROM grupo_alimentos_usuario WHERE id = ${id}`;
    return queryPromise(sql);
}

async function obterTotalCalorias(usuario_id, data) {
    // Fórmula: (caloria_base / porcao_base) * porcao_consumida
    let sql = `
        SELECT SUM((caloria_base / porcao_base) * porcao_consumida) as total
        FROM tb_alimentos_usuario
        WHERE usuario_id = ${usuario_id}
        AND DATE(data_registro) = '${data}'
    `;
    return queryPromise(sql);
}

module.exports = {
    listarGrupos,
    listarAlimentos,
    criarGrupo,
    apagarGrupo,
    buscarGrupoPorId,
    obterTotalCalorias // <--- Adicione aqui
};
