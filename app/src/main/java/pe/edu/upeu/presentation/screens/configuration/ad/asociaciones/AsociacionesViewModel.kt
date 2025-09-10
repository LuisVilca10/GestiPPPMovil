package pe.edu.upeu.presentation.screens.configuration.ad.asociaciones

/*
class AsociacionesViewModel (
    private val repository: AsociacionesRepository,
    private val repositoryMuni: MunicipalidadRepository,
    private val repositoryImgAso: ImgAsociacionesRepository,
    ) : ViewModel() {

    // Versión corregida
    private val _state = MutableStateFlow(AsociacionState())
    val state = _state.asStateFlow()

    private val _stateMuni = MutableStateFlow(MunicipalidadState())
    val stateMuni = _stateMuni.asStateFlow()

    private val _stateImgAso = MutableStateFlow(ImgAsoacionesState())
    val stateImgAso = _stateImgAso.asStateFlow()

    init {
        loadAllAsociaciones()
        loadAllImgAsoaciones()
        loadMunicipalidadCompleta()
    }

    fun loadAllAsociaciones(page: Int? = 0, searchQuery: String? = null) {
        viewModelScope.launch {
            // ⏳ Iniciamos el estado de carga
            _state.value = _state.value.copy(isLoading = true)

            try {
                println("🔄 [Asociaciones] Iniciando carga (página $page, búsqueda: $searchQuery)...")

                // Llamada directa a la página solicitada
                val response = repository.getAsociaciones(page = page ?: 0, name = searchQuery)

                response.onSuccess { res ->
                    println("✅ Página ${res.currentPage} cargada con éxito")
                    println("   ➕ Total registros recibidos: ${res.content.size}")
                    println("   🆔 IDs: ${res.content.map { it.id }}")

                    // Actualizamos el estado con la página recibida
                    _state.value = _state.value.copy(
                        itemsAso = res.content,
                        currentPage = res.currentPage,
                        totalPages = res.totalPages,
                        totalElements = res.totalElements,
                        isLoading = false,
                        error = null
                    )
                }.onFailure { error ->
                    throw error
                }

            } catch (e: Exception) {
                println("❌ Error al cargar asociaciones: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message,
                    notification = NotificationState(
                        message = e.message ?: "Error al cargar asociaciones",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }


    fun loadMunicipalidadCompleta(page: Int? = 0) {
        viewModelScope.launch {
            _stateMuni.update { it.copy(isLoading = true) }
            try {
                val response = repositoryMuni.getMunicipalidad(page = page ?: 0)
                response.onSuccess { res ->
                    _stateMuni.update {
                        it.copy(
                            items = res.content,
                            isLoading = false,
                            error = null
                        )
                    }
                }.onFailure { error ->
                    throw error
                }
            } catch (e: Exception) {
                _stateMuni.update {
                    it.copy(
                        isLoading = false,
                        error = e.message,
                        notification = NotificationState(
                            message = e.message ?: "Error al cargar municipalidades",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
            }
        }
    }







    // IMPORTANTE: Usa el DTO correcto (AsociacionUpdateDTO)
    fun updateAsociacion(asociacion: Asociacion) {
        viewModelScope.launch {
            println("🔄 [UPDATE] Iniciando actualización de Asociación...")
            // ... logs como tienes ahora...

            _state.update { it.copy(isLoading = true) }

            // Aseguramos que todas las imágenes tengan el asociacion_id correcto
            val imagenesUpdate = asociacion.imagenes?.map { img ->
                ImagenUpdateDTO(
                    id = img.id,
                    asociacion_id = asociacion.id!!, // SIEMPRE asigna el id de la asociacion
                    url_image = img.url_image ?: "",
                    estado = img.estado ?: true,
                    codigo = img.codigo ?: "",
                    description = img.description ?: ""
                )
            } ?: emptyList()
            val dto = AsociacionUpdateDTO(
                municipalidad_id = asociacion.municipalidadId ?: "",
                nombre = asociacion.nombre ?: "",
                descripcion = asociacion.descripcion ?: "",
                lugar = asociacion.lugar ?: "",
                phone = asociacion.phone ?: "",
                office_hours = asociacion.office_hours ?: "",
                url = asociacion.url ?: "",
                estado = asociacion.estado,
                imagenes = imagenesUpdate
            )

            if (asociacion.id != null) {
                println("📤 Enviando DTO para actualización: $dto")
                repository.updateAsociaciones(asociacion.id, dto)
                    .onSuccess {
                        println("✅ Asociación actualizada correctamente en el servidor.")
                        loadAllAsociaciones()
                        _state.update {
                            it.copy(
                                isDialogOpen = false,
                                selectedItem = null,
                                notification = NotificationState(
                                    message = "Asociación actualizada exitosamente",
                                    type = NotificationType.SUCCESS,
                                    isVisible = true
                                )
                            )
                        }
                    }
                    .onFailure { error ->
                        println("❌ Error actualizando Asociación en el servidor.")
                        println("   📩 Detalles del error: ${error.message}")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                notification = NotificationState(
                                    message = error.message ?: "Error al actualizar Asociación",
                                    type = NotificationType.ERROR,
                                    isVisible = true
                                )
                            )
                        }
                    }
            } else {
                println("❗ Datos incompletos para actualización:")
                println("   ➕ ID nulo: ${asociacion.id == null}")
                println("   ➕ DTO generado: $dto")
                _state.update {
                    it.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = "Datos incompletos para actualizar",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
            }
        }
    }



    fun deleteImgAsociaciones(id: String) {
        viewModelScope.launch {
            println("🗑️ Intentando eliminar Imagen de Asociacion con ID=$id")
            _state.value = _state.value.copy(isLoading = true)
            repositoryImgAso.deleteImgAsoaciones(id)
                .onSuccess {
                    println("✅ Imagen de Asociacion eliminada correctamente: ID=$id")
                    loadAllAsociaciones()
                    _state.value = _state.value.copy(
                        notification = NotificationState(
                            message = "Imagen de Asociacion eliminada exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
                .onFailure { error ->
                    println("❌ Error al eliminar Asociacion ID=$id: ${error.message}")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al eliminar Imagen de Asociacion",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }

    fun loadAllImgAsoaciones(searchQuery: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                println("🔄 Iniciando solicitud para cargar todas las imágenes de asociaciones...")
                println("   📄 Parámetros de búsqueda: $searchQuery")

                var currentPage = 0
                var totalPages = 1 // Inicializamos totalPages con un valor por defecto
                var allContent: List<ImgAsociaciones> = emptyList()

                do {
                    println("🔄 Cargando página $currentPage...")
                    val response = repositoryImgAso.getImgAsoaciones(page = currentPage, name = searchQuery)

                    response.onSuccess { res ->
                        totalPages = res.totalpages // Asignamos el valor real de totalPages
                        val content = res.content

                        // Concatenar las imágenes de la página actual
                        allContent = allContent + content

                        // 🔥 DEPURACIÓN COMPLETA AQUÍ 🔥
                        println("🛰️ Respuesta de la API recibida para la página $currentPage:")
                        println("   📦 Total imágenes de asociaciones en esta página: ${content.size}")
                        println("   🆔 IDs de asociaciones: ${content.map { it.id }}")
                        println("------------------------------------------------------------")
                    }.onFailure { error ->
                        throw error
                    }

                    // Incrementar la página para la siguiente solicitud
                    currentPage++

                } while (currentPage < totalPages)

                // Actualizar estado con todas las imágenes
                _stateImgAso.value = _stateImgAso.value.copy(
                    items = allContent,
                    currentPage = currentPage,
                    totalPages = totalPages,
                    isLoading = false,
                    error = null
                )

                // Confirmar que los datos fueron procesados correctamente
                println("✔️ Todas las imágenes de asociaciones cargadas correctamente.")

            } catch (e: Exception) {
                println("❌ Error al intentar obtener las imágenes de asociaciones.")
                println("   📩 Detalles del error: ${e.message}")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message,
                    notification = NotificationState(
                        message = e.message ?: "Error al cargar las imágenes de asociaciones",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }


    fun createImgAsociaciones(dto: ImgAsociacionesCreateDTO) {
        viewModelScope.launch {
            println("📤 [CREATE] Intentando crear Imagenes de Asociaciones...")
            println("   ➡️ Codigo: ${dto.codigo}")
            println("   ➡️ Estado: ${dto.estado}")
            println("   ➡️ Url_Image: ${dto.url_image}")
            println("   ➡️ Association: ${dto.asociacion_id}")

            _state.update { it.copy(isLoading = true) }

            repositoryImgAso.createImgAsoaciaciones(dto)
                .onSuccess {
                    println("✅ [CREATE] Imagen creada para la asociacion creada correctamente")
                    loadAllAsociaciones()
                    _state.update {
                        it.copy(
                            selectedItem = null,
                            isDialogOpen = false,
                            notification = NotificationState(
                                message = "Imagen de asociacion creada exitosamente",
                                type = NotificationType.SUCCESS,
                                isVisible = true
                            )
                        )
                    }
                }
                .onFailure { error ->
                    println("❌ [CREATE] Error al crear Imagen de Asociacion: ${error.message}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            notification = NotificationState(
                                message = error.message ?: "Error al crear Imagen de Asociacion",
                                type = NotificationType.ERROR,
                                isVisible = true
                            )
                        )
                    }
                }
        }
    }

    fun updateImgAsociaciones(asociaonesimg: ImgAsociaciones) {
        viewModelScope.launch {
            println("🔄 Intentando actualizar Imagen de Asociacion con ID=${asociaonesimg.id}")
            _state.value = _state.value.copy(isLoading = true)
            asociaonesimg.id?.let {
                repositoryImgAso.updateImgAsoaciones(it, asociaonesimg)
                    .onSuccess {
                        println("✅Imagen de Asociacion actualizada correctamente: ID=${asociaonesimg.id}")
                        loadAllAsociaciones()
                        _state.value = _state.value.copy(
                            isDialogOpen = false,
                            selectedItem = null,
                            notification = NotificationState(
                                message = "Asociacion actualizada exitosamente",
                                type = NotificationType.SUCCESS,
                                isVisible = true
                            )
                        )
                    }
                    .onFailure { error ->
                        println("❌ Error al actualizar Imagen de Asociacion ID=${asociaonesimg.id}: ${error.message}")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            notification = NotificationState(
                                message = error.message ?: "Error al actualizar Imagen de Asociacion",
                                type = NotificationType.ERROR,
                                isVisible = true
                            )
                        )
                    }
            }
        }
    }


    fun createAsociaciones(dto: AsociacionCreateDTO) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            repository.createAsociaciones(dto)
                .onSuccess { asociacion ->
                    println("✅ [VM] Asociación creada: $asociacion") // <-- Log en ViewModel
                    loadAllAsociaciones()
                    _state.update {
                        it.copy(
                            selectedItem = null,
                            isDialogOpen = false,
                            notification = NotificationState(
                                message = "Asociacion creada exitosamente",
                                type = NotificationType.SUCCESS,
                                isVisible = true
                            )
                        )
                    }
                }
                .onFailure { error ->
                    println("❌ [VM] Error al crear: ${error.message}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            notification = NotificationState(
                                message = error.message ?: "Error al crear Asociacion",
                                type = NotificationType.ERROR,
                                isVisible = true
                            )
                        )
                    }
                }
        }
    }



    fun deleteAsociaciones(id: String) {
        viewModelScope.launch {
            println("🗑️ Intentando eliminar Asociacion con ID=$id")
            _state.value = _state.value.copy(isLoading = true)
            repository.deleteAsociaciones(id)
                .onSuccess {
                    println("✅ Asociacion eliminada correctamente: ID=$id")
                    loadAllAsociaciones()
                    _state.value = _state.value.copy(
                        notification = NotificationState(
                            message = "Asociacion eliminada exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
                .onFailure { error ->
                    println("❌ Error al eliminar Asociacion ID=$id: ${error.message}")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al eliminar Asociacion",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }



    fun closeDialog() {
        _state.value = _state.value.copy(
            isDialogOpen = false,
            selectedItem = null
        )
    }



}
*/
