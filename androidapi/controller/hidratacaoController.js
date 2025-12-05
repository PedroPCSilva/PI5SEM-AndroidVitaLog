const { con } = require('../connection');

function queryPromise(sql) {
    return new Promise((resolve, reject) => {
        con.query(sql, (err, result) => {
            if (err) return reject(err);
            resolve(result);
        });
    });
}

// Listar registros de um dia específico
async function listar(data) {
    // A rota é /:usuarioId, então o Express manda 'usuarioId'.
    // O POST manda 'usuario_id' no corpo.
    // Essa linha garante que funcione dos dois jeitos:
    const id = data.usuarioId || data.usuario_id;

    let sql = `
        SELECT * FROM tb_hidratacao
        WHERE usuario_id = ${id}
        AND DATE(data_registro) = '${data.data}'
        ORDER BY data_registro DESC
    `;
    return queryPromise(sql);
}

// Adicionar registro (quantidade agora suporta DECIMAL 10,2 ex: 250.00)
async function adicionar(data) {
    let sql = `
        INSERT INTO tb_hidratacao (usuario_id, quantidade, data_registro)
        VALUES (${data.usuario_id}, ${data.quantidade}, NOW())
    `;
    return queryPromise(sql);
}

// Remover registro
async function remover(id) {
    return queryPromise(`DELETE FROM tb_hidratacao WHERE id = ${id}`);
}

module.exports = { listar, adicionar, remover };