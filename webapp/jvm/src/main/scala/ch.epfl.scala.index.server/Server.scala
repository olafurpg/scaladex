package ch.epfl.scala.index
package server

import api._

import model.Artifact

import akka.http.scaladsl._
import akka.http.scaladsl.model._, Uri._, StatusCodes.TemporaryRedirect

import de.heikoseeberger.akkahttpupickle.UpickleSupport
import upickle.default.{read => uread}

import com.softwaremill.session._
import com.softwaremill.session.CsrfDirectives._
import com.softwaremill.session.CsrfOptions._
import com.softwaremill.session.SessionDirectives._
import com.softwaremill.session.SessionOptions._

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.duration._
import scala.concurrent.Await

object Server extends UpickleSupport {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("scaladex")
    import system.dispatcher
    implicit val materializer = ActorMaterializer()

    val github = new Github

    val sessionConfig = SessionConfig.default("c05ll3lesrinf39t7mc5h6un6r0c69lgfno69dsak3vabeqamouq4328cuaekros401ajdpkh60rrtpd8ro24rbuqmgtnd1ebag6ljnb65i8a55d482ok7o0nch0bfbe")
    implicit val sessionManager = new SessionManager[UserState](sessionConfig)
    implicit val refreshTokenStorage = new InMemoryRefreshTokenStorage[UserState] {
      def log(msg: String) = println(msg)
    }

    val sharedApi = new ApiImplementation(github, None)

    def reuseSharedApi(userState: Option[UserState]) =
      if(userState.isDefined) new ApiImplementation(github, userState)
      else sharedApi
    
    val route = {
      import akka.http.scaladsl._
      import server.Directives._

      get {
        pathPrefix("api") {
          path("find") {
            parameters('query, 'start.as[Int] ? 0) { (query, start) =>
              complete(sharedApi.find(query, start))
            }
          } ~
          path("latest"){
            parameters('organization, 'name) { (organization, name) =>
              complete(sharedApi.latest(Artifact.Reference(organization, name)))
            }
          }
        }  
      } ~
      post {
        path("api" / Segments){ s ⇒
          entity(as[String]) { e ⇒
            optionalSession(refreshable, usingCookies) { userState =>
              complete {
                AutowireServer.route[Api](reuseSharedApi(userState))(
                  autowire.Core.Request(s, uread[Map[String, String]](e))
                )
              }
            }
          }
        }
      } ~
      get {
        path("login") {
          redirect(Uri("https://github.com/login/oauth/authorize").withQuery(Query(
            "client_id" -> github.clientId
          )),TemporaryRedirect)
        } ~
        path("logout") {
          requiredSession(refreshable, usingCookies) { _ =>
            invalidateSession(refreshable, usingCookies) { ctx =>
              ctx.complete("{}")
            }
          }
        } ~
        pathPrefix("callback") {
          path("done") {
            complete("OK")
          } ~
          pathEnd {
            parameter('code) { code =>
              val userState = Await.result(github.info(code), 10.seconds)
              setSession(refreshable, usingCookies, userState) {
                setNewCsrfToken(checkHeader) { ctx => 
                  ctx.complete("ok") 
                }
              }
              
              // A popup was open for Oauth2
              // We notify the opening window
              // We close the popup
              // complete(script("""|window.opener.oauth2()
              //                    |window.close()"""))
            }
          }
        } ~
        path("assets" / Rest) { path ⇒
          getFromResource(path)
        } ~
        pathSingleSlash {
          complete(Template.home)
        } ~
        path("project" / Rest) { _ ⇒
          complete(Template.home)
        }
      }
    }

    val setup = for {
      _ <- ApiImplementation.setup
      _ <- Http().bindAndHandle(route, "localhost", 8080)
    } yield ()
    Await.result(setup, 20.seconds)

    ()
  } 
}

