const { con } = require('../connection');

function queryPromise(sql) {
    return new Promise((resolve, reject) => {
        con.query(sql, (err, result) => {
            if (err) return reject(err);
            resolve(result);
        });
    });
}

async function getSemanal(usuario_id) {
    // Esta query pega os últimos 7 dias e soma calorias e hidratação agrupados por data
    // É uma query mais avançada usando UNION para juntar as duas tabelas
    let sql = `
        SELECT data_reg, SUM(calorias) as total_calorias, SUM(agua) as total_agua
        FROM (
            SELECT DATE(data_registro) as data_reg, (caloria_base/porcao_base)*porcao_consumida as calorias, 0 as agua
            FROM tb_alimentos_usuario WHERE usuario_id = ${usuario_id}
            
            UNION ALL
            
            SELECT DATE(data_registro) as data_reg, 0 as calorias, quantidade as agua
            FROM tb_hidratacao WHERE usuario_id = ${usuario_id}
        ) as uniao
        WHERE data_reg >= DATE(NOW()) - INTERVAL 7 DAY
        GROUP BY data_reg
        ORDER BY data_reg DESC
    `;
    return queryPromise(sql);
}

module.exports = { getSemanal };