package com.ec.launchix.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayuda y Soporte") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Sección de contacto rápido
            QuickContactSection()

            Spacer(modifier = Modifier.height(16.dp))

            // FAQ Section
            FAQSection()

            Spacer(modifier = Modifier.height(16.dp))

            // Recursos adicionales
            AdditionalResourcesSection()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QuickContactSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "¿Necesitas ayuda inmediata?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Nuestro equipo está disponible para ayudarte",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { /* Abrir chat */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chat en Vivo")
                }

                OutlinedButton(
                    onClick = { /* Llamar */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Llamar")
                }
            }
        }
    }
}

@Composable
fun FAQSection() {
    Column {
        Text(
            text = "Preguntas Frecuentes",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        val faqItems = listOf(
            FAQItem(
                question = "¿Cómo puedo realizar un pedido?",
                answer = "Para realizar un pedido, navega por nuestros productos, agrega los artículos que desees al carrito y procede al checkout. Necesitarás proporcionar una dirección de entrega y método de pago válido."
            ),
            FAQItem(
                question = "¿Cuáles son los métodos de pago aceptados?",
                answer = "Aceptamos tarjetas de crédito y débito (Visa, Mastercard), transferencias bancarias y pago contra entrega en efectivo. Todos los pagos son procesados de forma segura."
            ),
            FAQItem(
                question = "¿Cuánto tiempo tarda la entrega?",
                answer = "Los tiempos de entrega varían según tu ubicación. Generalmente entregamos en 30-60 minutos para pedidos locales. Te notificaremos el tiempo estimado al confirmar tu pedido."
            ),
            FAQItem(
                question = "¿Puedo modificar o cancelar mi pedido?",
                answer = "Puedes modificar o cancelar tu pedido dentro de los primeros 5 minutos después de confirmarlo. Después de ese tiempo, contáctanos y veremos qué podemos hacer."
            ),
            FAQItem(
                question = "¿Hay un monto mínimo de pedido?",
                answer = "Sí, tenemos un pedido mínimo de $10. Esto nos ayuda a cubrir los costos de entrega y mantener nuestros precios competitivos."
            ),
            FAQItem(
                question = "¿Qué hago si hay un problema con mi pedido?",
                answer = "Si tienes algún problema con tu pedido, contáctanos inmediatamente a través del chat en vivo o llámanos. Resolveremos cualquier inconveniente lo más pronto posible."
            )
        )

        faqItems.forEach { faq ->
            FAQItemCard(faq)
        }
    }
}

@Composable
fun FAQItemCard(faq: FAQItem) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        onClick = { isExpanded = !isExpanded }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Contraer" else "Expandir",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = faq.answer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AdditionalResourcesSection() {
    Column {
        Text(
            text = "Recursos Adicionales",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        HelpResourceItem(
            icon = Icons.Default.Description,
            title = "Términos de Servicio",
            subtitle = "Lee nuestros términos y condiciones",
            onClick = { }
        )

        HelpResourceItem(
            icon = Icons.Default.Security,
            title = "Política de Privacidad",
            subtitle = "Conoce cómo protegemos tus datos",
            onClick = { }
        )

        HelpResourceItem(
            icon = Icons.Default.LocalShipping,
            title = "Política de Entregas",
            subtitle = "Información sobre nuestras entregas",
            onClick = { }
        )

        HelpResourceItem(
            icon = Icons.Default.AttachMoney,
            title = "Política de Reembolsos",
            subtitle = "Conoce nuestra política de devoluciones",
            onClick = { }
        )

        HelpResourceItem(
            icon = Icons.Default.Feedback,
            title = "Enviar Comentarios",
            subtitle = "Ayúdanos a mejorar nuestro servicio",
            onClick = { }
        )
    }
}

@Composable
fun HelpResourceItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class FAQItem(
    val question: String,
    val answer: String
)