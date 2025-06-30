const { onSchedule } = require("firebase-functions/v2/scheduler");
const { onDocumentUpdated, onDocumentCreated } = require("firebase-functions/v2/firestore");
const { onRequest } = require("firebase-functions/v2/https");
const { onDocumentWritten } = require("firebase-functions/v2/firestore");
const { onObjectFinalized } = require("firebase-functions/v2/storage");
const logger = require("firebase-functions/logger");
const admin = require("firebase-admin");


admin.initializeApp();

function convertirHoraStringAMinutos(horaStr) {
  try {
    const [hora, minuto] = horaStr.split(":").map(Number);
    return hora * 60 + minuto;
  } catch (e) {
    console.error("❌ Error al parsear tiempoDeNotificacion:", horaStr);
    return null;
  }
}

exports.notificacionInactividadV2 = onSchedule("every 15 minutes", async (event) => {
  const snapshot = await admin.firestore().collection("usuarios").get();
  const ahora = new Date();
  const horaActual = ahora.getHours();
  const minutoActual = ahora.getMinutes();
  const hoyStr = ahora.toISOString().split("T")[0];

  snapshot.forEach(async (doc) => {
    const data = doc.data();
    const tiempoStr = data.tiempoDeNotificacion;
    const token = data.fcmToken;
    const notis = data.notificaciones || {};
    const fechaUltimaEntrada = data.fechaUltimaEntrada;

    if (!tiempoStr || typeof tiempoStr !== "string" || !token) return;
    if (notis.horaEntrada === false) return;
    if (fechaUltimaEntrada === hoyStr) {
      console.log("🟢 Usuario ya entró hoy, se omite notificación:", doc.id);
      return;
    }

    const minutosPromedio = convertirHoraStringAMinutos(tiempoStr);
    if (minutosPromedio === null) return;

    const minutosActuales = horaActual * 60 + minutoActual;
    const diferenciaMin = minutosActuales - minutosPromedio;

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

exports.notificacionLikeComentario = onDocumentUpdated("comentarios/{comentarioId}", async (event) => {
  const antes = event.data.before.data();
  const despues = event.data.after.data();

  // Solo continuar si aumentaron los likes
  if ((antes.likes || 0) >= (despues.likes || 0)) return;

  const autorId = despues.uidAutor;
  const quienDioLikeUid = despues.ultimoLikeUid;

  if (!autorId || !quienDioLikeUid || autorId === quienDioLikeUid) return;

  // Obtener documentos de autor y quien dio like
  const [autorDoc, quienDoc] = await Promise.all([
    admin.firestore().collection("usuarios").doc(autorId).get(),
    admin.firestore().collection("usuarios").doc(quienDioLikeUid).get()
  ]);

  if (!autorDoc.exists || !quienDoc.exists) return;

  const autorData = autorDoc.data();
  const quienData = quienDoc.data();

  // Verificar si el usuario acepta recibir notificaciones de likes
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


exports.notificacionDenuncia = onDocumentCreated("denuncias/{id}", async (event) => {
  const data = event.data.data();
  const autorId = data.autorContenido;
  const formato = data.formato;

  if (!autorId) {
    console.log("❌ No se especificó autorContenido en la denuncia.");
    return;
  }

  const doc = await admin.firestore().collection("usuarios").doc(autorId).get();
  const userData = doc.data();
  const token = userData?.fcmToken;

  if (!token) {
    console.log("⚠️ Usuario sin token FCM:", autorId);
    return;
  }

  const notificaciones = userData?.notificaciones || {};
  const tieneActiva = notificaciones.denunciaComentario ?? true;

  if (!tieneActiva) {
    console.log("🔕 Usuario tiene desactivadas las notificaciones de denuncia:", autorId);
    return;
  }

  let tipo = "contenido"; 
  if (formato === "comentario") {
    tipo = "comentario";
  } else if (formato === "clase") {
    tipo = "clase";
  } else if (formato === "curso") {
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