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
    // 1. Tenta buscar os grupos existentes
    let sql = `
        SELECT * FROM grupo_alimentos_usuario
        WHERE usuario_id = ${data.usuario_id}
        AND DATE(data_registro) = '${data.data}'
        ORDER BY id ASC
    `; // Mudei order by para ID para manter a ordem de criação (Café -> Jantar)
    
    let grupos = await queryPromise(sql);

    // 2. Se a lista estiver vazia (usuário abriu um dia novo), cria os padrões
    if (grupos.length === 0) {
        console.log("Dia vazio! Criando refeições padrão...");
        
        const padroes = ["Café da Manhã", "Almoço", "Lanche da Tarde", "Jantar"];
        
        for (const nomeRefeicao of padroes) {
            await queryPromise(`
                INSERT INTO grupo_alimentos_usuario (usuario_id, nome, data_registro)
                VALUES (${data.usuario_id}, '${nomeRefeicao}', '${data.data} 12:00:00')
            `);
        }

        // Busca de novo para retornar a lista preenchida
        grupos = await queryPromise(sql);
    }

    return grupos;
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
    // A função COALESCE garante que se a soma for NULL, ele retorna 0
    // Também adicionei proteção contra divisão por zero no banco
    let sql = `
        SELECT COALESCE(SUM((caloria_base / NULLIF(porcao_base, 0)) * porcao_consumida), 0) as total
        FROM tb_alimentos_usuario
        WHERE usuario_id = ${usuario_id}
        AND DATE(data_registro) = '${data}'
    `;
    
    // Pequeno ajuste para garantir que retornamos o valor exato dentro do objeto
    const resultado = await queryPromise(sql);
    return resultado[0]; 
}

module.exports = {
    listarGrupos,
    listarAlimentos,
    criarGrupo,
    apagarGrupo,
    buscarGrupoPorId,
    obterTotalCalorias // <--- Adicione aqui
};
