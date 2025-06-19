const { onSchedule } = require("firebase-functions/v2/scheduler");
const { onDocumentUpdated, onDocumentCreated } = require("firebase-functions/v2/firestore");
const { onRequest } = require("firebase-functions/v2/https");
const { onDocumentWritten } = require("firebase-functions/v2/firestore");
const { onObjectFinalized } = require("firebase-functions/v2/storage");
const logger = require("firebase-functions/logger");
const admin = require("firebase-admin");
admin.initializeApp();

// 🕒 Notificación por hora promedio
exports.notificacionInactividadV2 = onSchedule("every 15 minutes", async (event) => {
  const snapshot = await admin.firestore().collection("usuarios").get();
  const ahora = new Date();
  const horaActual = ahora.getHours();
  const minutoActual = ahora.getMinutes();

  snapshot.forEach(async (doc) => {
    const data = doc.data();
    const horaPromedio = data.horaPromedio;
    const token = data.fcmToken;
    const notis = data.notificaciones || {};

    // 🔒 Validaciones mínimas
    if (horaPromedio === undefined || token === undefined) return;
    if (notis.horaEntrada === false) return;

    // 🎯 Diferencia total en minutos
    const minutosActuales = horaActual * 60 + minutoActual;
    const minutosPromedio = horaPromedio * 60; // horaPromedio es int

    const diferenciaMin = minutosActuales - minutosPromedio;

    // ⏰ Si han pasado entre 20 y 30 minutos desde la hora promedio
    if (diferenciaMin >= 20 && diferenciaMin <= 30) {
      const message = {
        token,
        notification: {
          title: "🎶 ¡Hora de practicar!",
          body: "Parece que no entraste a tu hora habitual, ¡te esperamos en Skilful Harmony!",
        },
      };

      try {
        const res = await admin.messaging().send(message);
        console.log("✅ Notificación enviada a:", doc.id, res);
      } catch (error) {
        console.error("❌ Error al enviar notificación a:", doc.id, error);
      }
    }
  });
});

// ❤️ Notificación por like en comentario
exports.notificacionLikeComentario = onDocumentUpdated("comentarios/{comentarioId}", async (event) => {
  const antes = event.data.before.data();
  const despues = event.data.after.data();

  if ((antes.likes || 0) >= (despues.likes || 0)) return;

  const autorId = despues.autorId;
  const quienDioLikeUid = despues.ultimoLikeUid;

  if (!autorId || !quienDioLikeUid || autorId === quienDioLikeUid) return;

  const [autorDoc, quienDoc] = await Promise.all([
    admin.firestore().collection("usuarios").doc(autorId).get(),
    admin.firestore().collection("usuarios").doc(quienDioLikeUid).get()
  ]);

  if (!autorDoc.exists || !quienDoc.exists) return;

  const autorData = autorDoc.data();
  const quienData = quienDoc.data();

  if (!autorData?.notificaciones?.likeComentario) {
    console.log("🔕 Notificación de like desactivada por el usuario:", autorId);
    return;
  }

  const token = autorData.fcmToken;
  const nombreQuien = quienData?.nombre || "Alguien";

  if (!token) return;

  const message = {
    token: token,
    notification: {
      title: `¡${nombreQuien} le dio like a tu comentario!`,
      body: "Sigue compartiendo 🤘"
    }
  };

  try {
    const response = await admin.messaging().send(message);
    console.log("✅ Notificación enviada:", response);
  } catch (error) {
    console.error("❌ Error al enviar notificación:", error);
  }
});

// 🚨 Notificación por denuncia
exports.notificacionDenuncia = onDocumentCreated("denuncias/{id}", async (event) => {
  const data = event.data.data();
  const autorId = data.autorContenido;

  if (!autorId) {
    console.log("❌ No se especificó autorContenido en la denuncia.");
    return;
  }

  const doc = await admin.firestore().collection("usuarios").doc(autorId).get();
  const token = doc.data()?.fcmToken;

  if (!token) {
    console.log("⚠️ Usuario sin token FCM:", autorId);
    return;
  }

  let tipo = "contenido";

  if (typeof data.idComentario === "number" && data.idComentario !== -1) {
    tipo = "comentario";
  } else if (typeof data.idClase === "number" && data.idClase !== -1) {
    tipo = "clase";
  } else if (typeof data.idCurso === "number" && data.idClase === -1 && data.idComentario === -1) {
    tipo = "curso";
  }
  const message = {
    token: token,
    notification: {
      title: "⚠️ Uno de tus contenidos ha sido denunciado",
      body: `Tu ${tipo} fue reportado por otro usuario. Revísalo para evitar sanciones.`,
    }
  };

  try {
    const res = await admin.messaging().send(message);
    console.log("✅ Notificación de denuncia enviada a:", autorId, res);
  } catch (error) {
    console.error("❌ Error al enviar notificación de denuncia:", error);
  }
});

//🛡️ Ocultar contenido automáticamente por denuncias acumuladas
exports.ocultarContenidoDenunciado = onDocumentCreated("denuncias/{id}", async (event) => {
    const data = event.data.data();
    const contenidoId = data.contenidoId;
  
    // Ver cuántas denuncias tiene ese contenido
    const snapshot = await admin.firestore()
      .collection("denuncias")
      .where("contenidoId", "==", contenidoId)
      .get();
  
    if (snapshot.size >= 3) {
      // Ocultar automáticamente el contenido (ej. clase)
      await admin.firestore().collection("clases").doc(contenidoId).update({
        visible: false,
        ocultadoPor: "denuncias automáticas"
      });
    }
  });  
