const { onSchedule } = require("firebase-functions/v2/scheduler");
const { onDocumentUpdated, onDocumentCreated } = require("firebase-functions/v2/firestore");
const { onRequest } = require("firebase-functions/v2/https");
const { onDocumentWritten } = require("firebase-functions/v2/firestore");
const { onObjectFinalized } = require("firebase-functions/v2/storage");
const logger = require("firebase-functions/logger");
const admin = require("firebase-admin");
admin.initializeApp();

// 🕒 Notificación por hora promedio (RQF31)
exports.notificacionInactividadV2 = onSchedule("every 60 minutes", async (event) => {
  const snapshot = await admin.firestore().collection("usuarios").get();
  const horaActual = new Date().getHours();

  snapshot.forEach(async (doc) => {
    const data = doc.data();
    if (data.horaPromedio === horaActual - 1) {
      await admin.messaging().sendToDevice(data.fcmToken, {
        notification: {
          title: "¡Hora de practicar!",
          body: "Hace rato que no te conectas 🎶",
        },
      });
    }
  });
});

// ❤️ Notificación por like en comentario (RQF32)
exports.notificacionLikeComentario = onDocumentUpdated("comentarios/{comentarioId}", async (event) => {
  const antes = event.data.before.data();
  const despues = event.data.after.data();

  if ((antes.likes || 0) < (despues.likes || 0)) {
    const autorId = despues.autorId;

    const doc = await admin.firestore().collection("usuarios").doc(autorId).get();
    const userData = doc.data();

    if (!userData) {
      console.error("❌ No se encontró el usuario:", autorId);
      return;
    }

    // Verificamos si tiene desactivada la notificación de "me gusta en comentarios"
    const notificaciones = userData.notificaciones || {};
    if (notificaciones.likeComentario === false) {
      console.log("🔕 Notificación de like desactivada por el usuario:", autorId);
      return;
    }

    const token = userData.fcmToken;
    if (!token) {
      console.error("⚠️ Token FCM no encontrado para el usuario:", autorId);
      return;
    }

    const message = {
      token: token,
      notification: {
        title: "¡Le dieron like a tu comentario!",
        body: "Sigue compartiendo 🤘"
      }
    };

    try {
      const response = await admin.messaging().send(message);
      console.log("✅ Notificación enviada:", response);
    } catch (error) {
      console.error("❌ Error al enviar notificación:", error);
    }
  }
});


// 🚨 Notificación por denuncia (RQF33)
exports.notificacionDenuncia = onDocumentCreated("denuncias/{id}", async (event) => {
  const data = event.data.data();
  const autorId = data.autorContenido;

  const doc = await admin.firestore().collection("usuarios").doc(autorId).get();
  const token = doc.data().fcmToken;
  await admin.messaging().sendToDevice(token, {
    notification: {
      title: "Contenido denunciado ⚠️",
      body: "Revisa las políticas para evitar sanciones.",
    },
  });
});

//🧠 Recalcular vector del usuario cuando califica una clase

exports.recalcularVectorUsuario = onDocumentWritten("clases/{claseId}/calificaciones/{userId}", async (event) => {
  const calificacion = event.data?.after?.data()?.valor;
  const userId = event.params.userId;

  if (!calificacion) return;

  await admin.firestore().collection("usuarios").doc(userId).update({
    vectorAfinidad: admin.firestore.FieldValue.increment(calificacion),
    ultimaActualizacion: admin.firestore.Timestamp.now(),
  });
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
