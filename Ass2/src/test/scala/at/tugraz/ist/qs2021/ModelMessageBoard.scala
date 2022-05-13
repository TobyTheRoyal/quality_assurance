package at.tugraz.ist.qs2021

case class ModelMessageBoard(
                              messages: List[ModelUserMessage],
                              reports: List[ModelReport],
                              lastCommandSuccessful: Boolean,
                              userBanned: Boolean
                            ) {
  private def successStr: String = {
    if (lastCommandSuccessful) "success" else "fail"
  }

  private def bannedStr: String = {
    if (userBanned) "banned" else "not banned"
  }

  override def toString: String = s"ModelMessageBoard([${messages.mkString(",")}], [${reports.mkString(",")}], $successStr, $bannedStr)"
}

case class ModelReport(clientName: String, reportedClientName: String)

case class ModelUserMessage(
                             author: String,
                             message: String,
                             likes: List[String],
                             dislikes: List[String]
                           ) {
  override def toString = s"$author: $message; Likes: ${likes.sorted.mkString(",")}; Dislikes: ${dislikes.sorted.mkString(",")}"
}
