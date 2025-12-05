const { con } = require('../connection');

function queryPromise(sql) {
    return new Promise((resolve, reject) => {
        con.query(sql, (err, result) => {
            if (err) return reject(err);
            resolve(result);
        });
    });
}

async function adicionarAlimento(data) {
    let sql = `
        INSERT INTO tb_alimentos_usuario 
        (usuario_id, grupo_id, nome, caloria_base, porcao_consumida, porcao_base, data_registro)
        VALUES 
        (${data.usuario_id}, ${data.grupo_id}, '${data.nome}', ${data.caloria_base}, 
        ${data.porcao_consumida}, ${data.porcao_base}, NOW())
    `;
    return queryPromise(sql);
}

async function editarAlimento(id, data) {
    let sql = `
        UPDATE tb_alimentos_usuario
        SET nome='${data.nome}',
            caloria_base=${data.caloria_base},
            porcao_consumida=${data.porcao_consumida},
            porcao_base=${data.porcao_base}
        WHERE id=${id}
    `;
    return queryPromise(sql);
}

async function apagarAlimento(id) {
    return queryPromise(`DELETE FROM tb_alimentos_usuario WHERE id=${id}`);
}

async function buscarAlimentosBase(termo) {
    // Atenção: Usei LIKE para buscar partes do nome
    let sql = `
        SELECT * FROM tb_alimentos 
        WHERE nome LIKE '%${termo}%' 
        LIMIT 20
    `;
    return queryPromise(sql);
}

module.exports = {
    adicionarAlimento,
    editarAlimento,
    apagarAlimento,
    buscarAlimentosBase
}
