package model

data class NodeType(
    val name: String,
    val regex: Regex,
    val isEnabled: Boolean,
    val shape: String,
    val fillColor: String
)