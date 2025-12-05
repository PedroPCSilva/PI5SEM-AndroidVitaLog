const { con } = require('../connection');

function queryPromise(sql) {
    return new Promise((resolve, reject) => {
        con.query(sql, (err, result) => {
            if (err) return reject(err);
            resolve(result);
        });
    });
}

// Listar metas de um dia específico
async function listar(usuario_id, data) {
    let sql = `
        SELECT * FROM tb_metas_usuario
        WHERE usuario_id = ${usuario_id}
        AND DATE(data_registro) = '${data}'
    `;
    return queryPromise(sql);
}

// Define (Cria ou Atualiza) uma meta para o dia de HOJE
async function definir(data) {
    console.log("Recebido no Backend:", data); // <--- Mostra o que chegou do Android

    // Garante que usuario_id é válido
    if (!data.usuario_id || data.usuario_id == 0) {
        throw new Error("ID do usuário inválido ou zero.");
    }

    // 1. Verifica se já existe meta hoje
    let checkSql = `
        SELECT id FROM tb_metas_usuario 
        WHERE usuario_id = ${data.usuario_id} 
        AND tipo = '${data.tipo}' 
        AND DATE(data_registro) = DATE(NOW())
    `;
    
    try {
        let exists = await queryPromise(checkSql);

        if (exists.length > 0) {
            // UPDATE
            console.log("Atualizando meta existente ID:", exists[0].id);
            let updateSql = `
                UPDATE tb_metas_usuario 
                SET meta = ${data.meta} 
                WHERE id = ${exists[0].id}
            `;
            await queryPromise(updateSql);
            return { message: "Meta atualizada", id: exists[0].id };
        } else {
            // INSERT
            console.log("Criando nova meta...");
            let insertSql = `
                INSERT INTO tb_metas_usuario (usuario_id, tipo, meta, data_registro)
                VALUES (${data.usuario_id}, '${data.tipo}', ${data.meta}, NOW())
            `;
            let result = await queryPromise(insertSql);
            return { message: "Meta criada", id: result.insertId };
        }
    } catch (err) {
        console.error("ERRO SQL:", err); // <--- Mostra o erro exato do banco
        throw err; // Repassa o erro para o server.js devolver 500
    }
}
module.exports = { listar, definir };