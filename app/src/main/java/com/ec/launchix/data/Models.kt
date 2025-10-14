
package com.ec.launchix.data

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double? = null,
    val imageUrl: String,
    val category: String,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val isOnSale: Boolean = false,
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList()
)

data class Service(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double? = null,
    val imageUrl: String,
    val imageUrls: List<String> = listOf(),
    val category: String,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val duration: String = "",
    val isOnSale: Boolean = false,
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList()
)

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val itemCount: Int = 0
)

object SampleData {
    val sampleCategories = listOf(
        Category("1", "Electrónicos", "📱", "red", 35),
        Category("2", "Ropa", "👕", "blue", 28),
        Category("3", "Hogar", "🏠", "green", 22),
        Category("4", "Deportes", "⚽", "orange", 18),
        Category("5", "Belleza", "💄", "pink", 25),
        Category("6", "Salud", "💊", "purple", 17),
        Category("7", "Comida", "🍔", "yellow", 30),
        Category("8", "Libros", "📚", "indigo", 15),
        Category("9", "Automóvil", "🚗", "gray", 12)
    )

    val sampleProducts = listOf(
        // ELECTRÓNICOS
        Product(
            id = "p1",
            name = "iPhone 15 Pro",
            description = "El iPhone más avanzado con chip A17 Pro y cámara de 48MP",
            price = 999.0,
            originalPrice = 1199.0,
            imageUrl = "https://images.unsplash.com/photo-1678652197831-2d180705cd2c?w=400&h=400&fit=crop",
            category = "Electrónicos",
            rating = 4.8f,
            reviewCount = 1299,
            isOnSale = true,
            isFavorite = true,
            tags = listOf("Nuevo", "Premium", "Oferta")
        ),
        Product(
            id = "p2",
            name = "Samsung Galaxy S23",
            description = "Smartphone Android premium con S Pen y cámara de 200MP",
            price = 879.0,
            originalPrice = 1099.0,
            imageUrl = "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=400&h=400&fit=crop",
            category = "Electrónicos",
            rating = 4.7f,
            reviewCount = 856,
            isOnSale = true,
            tags = listOf("Android", "S Pen", "Cámara Pro")
        ),
        Product(
            id = "p3",
            name = "MacBook Air M3",
            description = "Laptop ultradelgada con chip Apple M3 y pantalla Liquid Retina",
            price = 1199.0,
            imageUrl = "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=400&fit=crop",
            category = "Electrónicos",
            rating = 4.9f,
            reviewCount = 445,
            tags = listOf("Apple", "M3", "Ultradelgada")
        ),
        Product(
            id = "p4",
            name = "AirPods Pro 2",
            description = "Auriculares inalámbricos con cancelación de ruido adaptativa",
            price = 229.0,
            originalPrice = 249.0,
            imageUrl = "https://images.unsplash.com/photo-1606841837239-c5a1a4a07af7?w=400&h=400&fit=crop",
            category = "Electrónicos",
            rating = 4.6f,
            reviewCount = 1567,
            isOnSale = true,
            tags = listOf("Apple", "Noise Cancelling", "Inalámbricos")
        ),
        Product(
            id = "p5",
            name = "iPad Pro 12.9\"",
            description = "Tablet profesional con chip M2 y pantalla Liquid Retina XDR",
            price = 1099.0,
            imageUrl = "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400&h=400&fit=crop",
            category = "Electrónicos",
            rating = 4.8f,
            reviewCount = 334,
            tags = listOf("Tablet", "M2", "Profesional")
        ),
        Product(
            id = "p6",
            name = "Sony WH-1000XM5",
            description = "Auriculares premium con la mejor cancelación de ruido",
            price = 349.0,
            imageUrl = "https://images.unsplash.com/photo-1545127398-14699f92334b?w=400&h=400&fit=crop",
            category = "Electrónicos",
            rating = 4.8f,
            reviewCount = 789,
            tags = listOf("Sony", "Premium", "Noise Cancelling")
        ),
        Product(
            id = "p7",
            name = "Nintendo Switch OLED",
            description = "Consola híbrida con pantalla OLED de 7 pulgadas",
            price = 349.0,
            imageUrl = "https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?w=400&h=400&fit=crop",
            category = "Electrónicos",
            rating = 4.7f,
            reviewCount = 623,
            tags = listOf("Gaming", "OLED", "Híbrida")
        ),
        Product(
            id = "p8",
            name = "Apple Watch Series 9",
            description = "Smartwatch con GPS, monitor de salud y pantalla Always-On",
            price = 399.0,
            originalPrice = 429.0,
            imageUrl = "https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=400&h=400&fit=crop",
            category = "Electrónicos",
            rating = 4.5f,
            reviewCount = 912,
            isOnSale = true,
            tags = listOf("Smartwatch", "Salud", "GPS")
        ),

        // ROPA
        Product(
            id = "p9",
            name = "Camiseta Premium",
            description = "Camiseta de algodón orgánico, suave y cómoda",
            price = 29.0,
            originalPrice = 39.0,
            imageUrl = "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400&h=400&fit=crop",
            category = "Ropa",
            rating = 4.5f,
            reviewCount = 542,
            isOnSale = true,
            isFavorite = true,
            tags = listOf("Algodón", "Cómoda")
        ),
        Product(
            id = "p10",
            name = "Jeans Levi's 501",
            description = "Jeans clásicos de corte recto, 100% algodón",
            price = 79.0,
            imageUrl = "https://images.unsplash.com/photo-1475178626620-a4d074967452?w=400&h=400&fit=crop",
            category = "Ropa",
            rating = 4.6f,
            reviewCount = 1234,
            tags = listOf("Clásico", "Algodón", "Levi's")
        ),
        Product(
            id = "p11",
            name = "Nike Air Max 90",
            description = "Zapatillas icónicas con amortiguación Air Max visible",
            price = 129.0,
            originalPrice = 149.0,
            imageUrl = "https://images.unsplash.com/photo-1600185365926-3a2ce3cdb9eb?w=400&h=400&fit=crop",
            category = "Ropa",
            rating = 4.7f,
            reviewCount = 889,
            isOnSale = true,
            tags = listOf("Nike", "Air Max", "Icónicas")
        ),
        Product(
            id = "p12",
            name = "Chaqueta de Cuero",
            description = "Chaqueta de cuero genuino con forro interior acolchado",
            price = 299.0,
            originalPrice = 399.0,
            imageUrl = "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400&h=400&fit=crop",
            category = "Ropa",
            rating = 4.8f,
            reviewCount = 156,
            isOnSale = true,
            tags = listOf("Cuero", "Premium", "Clásica")
        ),
        Product(
            id = "p13",
            name = "Vestido Elegante",
            description = "Vestido negro elegante perfecto para ocasiones especiales",
            price = 89.0,
            imageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=400&h=400&fit=crop",
            category = "Ropa",
            rating = 4.4f,
            reviewCount = 267,
            tags = listOf("Elegante", "Negro", "Especial")
        ),
        Product(
            id = "p14",
            name = "Sudadera con Capucha",
            description = "Sudadera cómoda con capucha y bolsillo frontal",
            price = 49.0,
            imageUrl = "https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=400&h=400&fit=crop",
            category = "Ropa",
            rating = 4.3f,
            reviewCount = 445,
            tags = listOf("Cómoda", "Capucha", "Casual")
        ),

        // HOGAR
        Product(
            id = "p15",
            name = "Sofá Moderno 3 Plazas",
            description = "Sofá de 3 plazas con diseño minimalista",
            price = 599.0,
            imageUrl = "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400&h=400&fit=crop",
            category = "Hogar",
            rating = 4.7f,
            reviewCount = 89,
            tags = listOf("Moderno", "Cómodo")
        ),
        Product(
            id = "p16",
            name = "Smart TV Samsung 55\"",
            description = "Televisor 4K UHD con sistema operativo Tizen",
            price = 679.0,
            originalPrice = 799.0,
            imageUrl = "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=400&h=400&fit=crop",
            category = "Hogar",
            rating = 4.6f,
            reviewCount = 567,
            isOnSale = true,
            tags = listOf("4K", "Smart", "Samsung")
        ),
        Product(
            id = "p17",
            name = "Cafetera Espresso",
            description = "Cafetera automática con molinillo integrado",
            price = 299.0,
            imageUrl = "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=400&h=400&fit=crop",
            category = "Hogar",
            rating = 4.5f,
            reviewCount = 234,
            tags = listOf("Automática", "Espresso", "Molinillo")
        ),
        Product(
            id = "p18",
            name = "Aspiradora Robot",
            description = "Aspiradora inteligente con navegación láser",
            price = 399.0,
            originalPrice = 499.0,
            imageUrl = "https://images.unsplash.com/photo-1558317374-067fb5f30001?w=400&h=400&fit=crop",
            category = "Hogar",
            rating = 4.4f,
            reviewCount = 378,
            isOnSale = true,
            tags = listOf("Robot", "Inteligente", "Láser")
        ),
        Product(
            id = "p19",
            name = "Lámpara LED Moderna",
            description = "Lámpara de pie con iluminación LED regulable",
            price = 89.0,
            imageUrl = "https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=400&h=400&fit=crop",
            category = "Hogar",
            rating = 4.3f,
            reviewCount = 123,
            tags = listOf("LED", "Regulable", "Moderna")
        ),

        // DEPORTES
        Product(
            id = "p20",
            name = "Bicicleta de Montaña",
            description = "Bicicleta MTB con suspensión delantera y 21 velocidades",
            price = 499.0,
            imageUrl = "https://images.unsplash.com/photo-1576435728678-68d0fbf94e91?w=400&h=400&fit=crop",
            category = "Deportes",
            rating = 4.6f,
            reviewCount = 189,
            tags = listOf("MTB", "Suspensión", "21 velocidades")
        ),
        Product(
            id = "p21",
            name = "Pesas Ajustables",
            description = "Set de pesas ajustables de 2 a 24 kg cada una",
            price = 199.0,
            imageUrl = "https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?w=400&h=400&fit=crop",
            category = "Deportes",
            rating = 4.7f,
            reviewCount = 345,
            tags = listOf("Ajustables", "24kg", "Set")
        ),
        Product(
            id = "p22",
            name = "Yoga Mat Premium",
            description = "Esterilla de yoga antideslizante de 6mm",
            price = 39.0,
            imageUrl = "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=400&h=400&fit=crop",
            category = "Deportes",
            rating = 4.5f,
            reviewCount = 567,
            tags = listOf("Yoga", "Antideslizante", "6mm")
        ),
        Product(
            id = "p23",
            name = "Balón de Fútbol",
            description = "Balón oficial FIFA para competiciones profesionales",
            price = 45.0,
            originalPrice = 55.0,
            imageUrl = "https://images.unsplash.com/photo-1614632537197-38a17061c2bd?w=400&h=400&fit=crop",
            category = "Deportes",
            rating = 4.8f,
            reviewCount = 234,
            isOnSale = true,
            tags = listOf("FIFA", "Oficial", "Profesional")
        ),

        // BELLEZA
        Product(
            id = "p24",
            name = "Set de Maquillaje",
            description = "Kit completo con paletas, brochas y productos esenciales",
            price = 89.0,
            originalPrice = 129.0,
            imageUrl = "https://images.unsplash.com/photo-1512496015851-a90fb38ba796?w=400&h=400&fit=crop",
            category = "Belleza",
            rating = 4.6f,
            reviewCount = 456,
            isOnSale = true,
            tags = listOf("Completo", "Paletas", "Brochas")
        ),
        Product(
            id = "p25",
            name = "Perfume Chanel",
            description = "Fragancia icónica femenina con notas florales",
            price = 149.0,
            imageUrl = "https://images.unsplash.com/photo-1541643600914-78b084683601?w=400&h=400&fit=crop",
            category = "Belleza",
            rating = 4.8f,
            reviewCount = 789,
            tags = listOf("Icónica", "Floral", "Premium")
        ),
        Product(
            id = "p26",
            name = "Crema Anti-edad",
            description = "Crema facial con retinol y ácido hialurónico",
            price = 59.0,
            imageUrl = "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=400&h=400&fit=crop",
            category = "Belleza",
            rating = 4.4f,
            reviewCount = 234,
            tags = listOf("Retinol", "Anti-edad", "Facial")
        ),
        Product(
            id = "p27",
            name = "Shampoo Profesional",
            description = "Shampoo profesional para cabello teñido y dañado",
            price = 29.0,
            imageUrl = "https://images.unsplash.com/photo-1631729371254-42c2892f0e6e?w=400&h=400&fit=crop",
            category = "Belleza",
            rating = 4.3f,
            reviewCount = 345,
            tags = listOf("Profesional", "Teñido", "Reparador")
        ),

        // COMIDA
        Product(
            id = "p30",
            name = "Sushi Variado",
            description = "Bandeja de sushi con 12 piezas variadas del chef",
            price = 25.0,
            originalPrice = 30.0,
            imageUrl = "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=400&h=400&fit=crop",
            category = "Comida",
            rating = 4.7f,
            reviewCount = 234,
            isOnSale = true,
            tags = listOf("Variado", "12 piezas", "Chef")
        )
    )

    val sampleServices = listOf(
        // s1 - Servicio de Limpieza
        Service(
            id = "s1",
            name = "Servicio de Limpieza",
            description = "Limpieza profunda de tu hogar u oficina",
            price = 80.0,
            originalPrice = 100.0,
            imageUrl = "https://images.unsplash.com/photo-1527515637462-cff94eecc1ac?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1527515637462-cff94eecc1ac?w=800",
                "https://images.unsplash.com/photo-1628177142898-93e36e4e3a50?w=800",
                "https://images.unsplash.com/photo-1585421514738-01798e348b17?w=800",
                "https://images.unsplash.com/photo-1563453392212-326f5e854473?w=800"
            ),
            category = "Hogar",
            rating = 4.9f,
            reviewCount = 234,
            duration = "3-4 horas",
            isOnSale = true,
            isFavorite = true,
            tags = listOf("Profesional", "Confiable")
        ),
        // s2 - Plomería Residencial
        Service(
            id = "s2",
            name = "Plomería Residencial",
            description = "Reparación y mantenimiento de tuberías y grifos",
            price = 65.0,
            imageUrl = "https://images.unsplash.com/photo-1607472586893-edb57bdc0e39?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1607472586893-edb57bdc0e39?w=800",
                "https://share.google/images/EBvzOL3PDVVU1alHj",
                "https://images.unsplash.com/photo-1604709177225-055f99402ea3?w=800",
                "https://images.unsplash.com/photo-1580674285054-bed31e145f59?w=800"
            ),
            category = "Hogar",
            rating = 4.7f,
            reviewCount = 189,
            duration = "2-3 horas",
            tags = listOf("Reparación", "Mantenimiento", "Urgente")
        ),
        // s3 - Electricista Certificado
        Service(
            id = "s3",
            name = "Electricista Certificado",
            description = "Instalación y reparación eléctrica residencial",
            price = 75.0,
            imageUrl = "https://images.unsplash.com/photo-1621905251918-48416bd8575a?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1621905251918-48416bd8575a?w=800",
                "https://images.unsplash.com/photo-1621905252472-2a26e2b0e3b0?w=800",
                "https://images.unsplash.com/photo-1473830394358-91588751b241?w=800",
                "https://images.unsplash.com/photo-1595846519845-68e298c2edd8?w=800"
            ),
            category = "Hogar",
            rating = 4.8f,
            reviewCount = 167,
            duration = "1-4 horas",
            tags = listOf("Certificado", "Instalación", "Seguro")
        ),
        // s4 - Jardinería y Paisajismo
        Service(
            id = "s4",
            name = "Jardinería y Paisajismo",
            description = "Diseño, mantenimiento y cuidado de jardines",
            price = 90.0,
            originalPrice = 110.0,
            imageUrl = "https://images.unsplash.com/photo-1558904541-efa843a96f01?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1558904541-efa843a96f01?w=800",
                "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=800",
                "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=800",
                "https://images.unsplash.com/photo-1592419044706-39796d40f98c?w=800"
            ),
            category = "Hogar",
            rating = 4.6f,
            reviewCount = 123,
            duration = "4-6 horas",
            isOnSale = true,
            tags = listOf("Diseño", "Mantenimiento", "Paisajismo")
        ),
        // s5 - Entrenamiento Personal
        Service(
            id = "s5",
            name = "Entrenamiento",
            description = "Sesión personalizada de entrenamiento físico",
            price = 45.0,
            imageUrl = "https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=800",
                "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=800",
                "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=800",
                "https://images.unsplash.com/photo-1549060279-7e168fcee0c2?w=800"
            ),
            category = "Deportes",
            rating = 4.8f,
            reviewCount = 167,
            duration = "1 hora",
            tags = listOf("Personalizado", "Fitness")
        ),
        // s6 - Clases de Yoga
        Service(
            id = "s6",
            name = "Clases de Yoga",
            description = "Clases grupales de yoga para todos los niveles",
            price = 25.0,
            imageUrl = "https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=800",
                "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=800",
                "https://images.unsplash.com/photo-1545389336-cf090694435e?w=800",
                "https://images.unsplash.com/photo-1588286840104-8957b019727f?w=800"
            ),
            category = "Deportes",
            rating = 4.7f,
            reviewCount = 234,
            duration = "1.5 horas",
            tags = listOf("Grupal", "Todos niveles", "Relajación")
        ),
        // s7 - Masaje Terapéutico
        Service(
            id = "s7",
            name = "Masaje Terapéutico",
            description = "Masaje profesional para alivio del estrés y tensión muscular",
            price = 70.0,
            originalPrice = 85.0,
            imageUrl = "https://images.unsplash.com/photo-1519823551278-64ac92734fb1?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1519823551278-64ac92734fb1?w=800",
                "https://images.unsplash.com/photo-1600334129128-685c5582fd35?w=800",
                "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=800",
                "https://images.unsplash.com/photo-1507652313519-d4e9174996dd?w=800"
            ),
            category = "Salud",
            rating = 4.9f,
            reviewCount = 345,
            duration = "1 hora",
            isOnSale = true,
            tags = listOf("Terapéutico", "Relajante", "Profesional")
        ),
        // s8 - Nutrición Deportiva
        Service(
            id = "s8",
            name = "Nutrición Deportiva",
            description = "Consulta nutricional especializada para deportistas",
            price = 55.0,
            imageUrl = "https://images.unsplash.com/photo-1498837167922-ddd27525d352?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1498837167922-ddd27525d352?w=800",
                "https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=800",
                "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800",
                "https://images.unsplash.com/photo-1547592180-85f173990554?w=800"
            ),
            category = "Salud",
            rating = 4.6f,
            reviewCount = 98,
            duration = "45 minutos",
            tags = listOf("Deportiva", "Especializada", "Consulta")
        ),
        // s9 - Consultoría de Negocios
        Service(
            id = "s9",
            name = "Consultoría de Negocios",
            description = "Asesoramiento estratégico para tu emprendimiento",
            price = 150.0,
            imageUrl = "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=800",
                "https://images.unsplash.com/photo-1542744173-8e7e53415bb0?w=800",
                "https://images.unsplash.com/photo-1553877522-43269d4ea984?w=800",
                "https://images.unsplash.com/photo-1556761175-4b46a572b786?w=800"
            ),
            category = "Negocios",
            rating = 4.6f,
            reviewCount = 45,
            duration = "2 horas",
            tags = listOf("Estrategia", "Negocios")
        ),
        // s10 - Diseño Gráfico
        Service(
            id = "s10",
            name = "Diseño Gráfico",
            description = "Creación de logotipos, branding y material publicitario",
            price = 120.0,
            originalPrice = 150.0,
            imageUrl = "https://images.unsplash.com/photo-1626785774573-4b799315345d?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1626785774573-4b799315345d?w=800",
                "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=800",
                "https://images.unsplash.com/photo-1609921212029-bb5a28e60960?w=800",
                "https://images.unsplash.com/photo-1572044162444-ad60f128bdea?w=800"
            ),
            category = "Negocios",
            rating = 4.7f,
            reviewCount = 156,
            duration = "3-5 días",
            isOnSale = true,
            tags = listOf("Logotipos", "Branding", "Publicitario")
        ),
        // s11 - Desarrollo Web
        Service(
            id = "s11",
            name = "Desarrollo Web",
            description = "Creación de sitios web responsivos y modernos",
            price = 300.0,
            imageUrl = "https://images.unsplash.com/photo-1547658719-da2b51169166?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1547658719-da2b51169166?w=800",
                "https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=800",
                "https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=800",
                "https://images.unsplash.com/photo-1484417894907-623942c8ee29?w=800"
            ),
            category = "Negocios",
            rating = 4.8f,
            reviewCount = 89,
            duration = "1-2 semanas",
            tags = listOf("Responsivo", "Moderno", "Web")
        ),
        // s12 - Fotografía de Eventos
        Service(
            id = "s12",
            name = "Fotografía de Eventos",
            description = "Cobertura fotográfica profesional para eventos especiales",
            price = 250.0,
            originalPrice = 300.0,
            imageUrl = "https://images.unsplash.com/photo-1554048612-b6a482bc67e5?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1554048612-b6a482bc67e5?w=800",
                "https://images.unsplash.com/photo-1511285560929-80b456fea0bc?w=800",
                "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=800",
                "https://images.unsplash.com/photo-1519741497674-611481863552?w=800"
            ),
            category = "Negocios",
            rating = 4.9f,
            reviewCount = 234,
            duration = "4-8 horas",
            isOnSale = true,
            tags = listOf("Profesional", "Eventos", "Especiales")
        ),
        // s13 - Corte y Peinado
        Service(
            id = "s13",
            name = "Corte y Peinado",
            description = "Servicio completo de peluquería y estilismo",
            price = 35.0,
            imageUrl = "https://images.unsplash.com/photo-1562322140-8baeececf3df?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1562322140-8baeececf3df?w=800",
                "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=800",
                "https://images.unsplash.com/photo-1560066984-138dadb4c035?w=800",
                "https://images.unsplash.com/photo-1595475207225-428b62bda831?w=800"
            ),
            category = "Belleza",
            rating = 4.5f,
            reviewCount = 567,
            duration = "1-2 horas",
            tags = listOf("Completo", "Estilismo", "Peluquería")
        ),
        // s14 - Manicure y Pedicure
        Service(
            id = "s14",
            name = "Manicure y Pedicure",
            description = "Cuidado completo de uñas con esmaltado profesional",
            price = 40.0,
            originalPrice = 50.0,
            imageUrl = "https://images.unsplash.com/photo-1610992015732-2449b76344bc?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1610992015732-2449b76344bc?w=800",
                "https://images.unsplash.com/photo-1604654894610-df63bc536371?w=800",
                "https://images.unsplash.com/photo-1519014816548-bf5fe059798b?w=800",
                "https://images.unsplash.com/photo-1607779097040-26e80aa78e66?w=800"
            ),
            category = "Belleza",
            rating = 4.6f,
            reviewCount = 345,
            duration = "1.5 horas",
            isOnSale = true,
            tags = listOf("Completo", "Esmaltado", "Profesional")
        ),
        // s15 - Tratamiento Facial
        Service(
            id = "s15",
            name = "Tratamiento Facial",
            description = "Limpieza facial profunda con productos premium",
            price = 80.0,
            imageUrl = "https://images.unsplash.com/photo-1516975080664-ed2fc6a32937?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1516975080664-ed2fc6a32937?w=800",
                "https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?w=800",
                "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=800",
                "https://images.unsplash.com/photo-1487412947147-5cebf100ffc2?w=800"
            ),
            category = "Belleza",
            rating = 4.8f,
            reviewCount = 234,
            duration = "1.5 horas",
            tags = listOf("Limpieza", "Premium", "Profunda")
        ),
        // s16 - Lavado Premium de Auto
        Service(
            id = "s16",
            name = "Lavado Premium de Auto",
            description = "Lavado completo exterior e interior con encerado",
            price = 30.0,
            imageUrl = "https://images.unsplash.com/photo-1520340356584-f9917d1eea6f?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1520340356584-f9917d1eea6f?w=800",
                "https://images.unsplash.com/photo-1601362840469-51e4d8d58785?w=800",
                "https://images.unsplash.com/photo-1619642751034-765dfdf7c58e?w=800",
                "https://images.unsplash.com/photo-1607860108855-64acf2078ed9?w=800"
            ),
            category = "Transporte",
            rating = 4.4f,
            reviewCount = 456,
            duration = "45 minutos",
            tags = listOf("Completo", "Encerado", "Premium")
        ),
        // s17 - Revisión Mecánica
        Service(
            id = "s17",
            name = "Revisión Mecánica",
            description = "Diagnóstico completo del estado de tu vehículo",
            price = 50.0,
            imageUrl = "https://images.unsplash.com/photo-1625047509168-a7026f36de04?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1625047509168-a7026f36de04?w=800",
                "https://images.unsplash.com/photo-1486262715619-67b85e0b08d3?w=800",
                "https://images.unsplash.com/photo-1632823469850-1b19ea5e17e8?w=800",
                "https://images.unsplash.com/photo-1487754180451-c456f719a1fc?w=800"
            ),
            category = "Transporte",
            rating = 4.7f,
            reviewCount = 189,
            duration = "1-2 horas",
            tags = listOf("Diagnóstico", "Completo", "Profesional")
        ),
        // s20 - Tutorías de Matemáticas
        Service(
            id = "s20",
            name = "Tutorías de Matemáticas",
            description = "Apoyo académico en matemáticas para estudiantes",
            price = 20.0,
            imageUrl = "https://images.unsplash.com/photo-1596496050827-8299e0220de1?w=800",
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1596496050827-8299e0220de1?w=800",
                "https://images.unsplash.com/photo-1509228468518-180dd4864904?w=800",
                "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=800",
                "https://images.unsplash.com/photo-1501504905252-473c47e087f8?w=800"
            ),
            category = "Educación",
            rating = 4.7f,
            reviewCount = 167,
            duration = "1 hora",
            tags = listOf("Académico", "Estudiantes", "Matemáticas")
        )
    )
}