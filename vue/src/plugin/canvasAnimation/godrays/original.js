// Currently in Chrome you need to click the "Get Adobe Flash" button so it'll ask you if you want to
// allow flash to run.

// The rest of this is formatted as:
//    // Explanation of the compressed code
//    // ...
//
//    // Commented out, compressed code
//
//    Readable (more or less) version of the code
//    ...

window.addEventListener("DOMContentLoaded", go)

function go() {
    "use strict"
    // JS1K's HTML shim gives us a canvas (a) and its 2D context (c) for free. We'll set them up here.

    let canvas = document.querySelector('canvas')
    let ctx = canvas.getContext('2d')

    // First off - we define an abbreviation function. This takes an object, iterates over its properties
    // and stores their names as strings in a 2 or 3 letter variable ("this" is the window object).
    //
    // p[0]+p[6] will evaluate to the 1st and 7th letter (or the 1st+"undefined" if there's no 7th),
    // [p[20]] will be an empty string if the property's name is too short ([undefined] gets coerced to
    // an empty string).
    //
    // This is a variation on Marijn Haverbeke's technique - see https://marijnhaverbeke.nl/js1k/
    //
    // We won't be using it in the readable version of the demo.

    // A=o=>{for(p in o)this[p[0]+p[6]+[p[20]]]=p}

    // Next we abbreviate all the properties in our window object because requestAnimationFrame() is
    // kind of long. We can't call A(window) because it will try to abbreviate all our abbreviations (since
    // it stores them in the window object) so we'll use it on "top" which has the same properties.
    // We really just need a shorter requestAnimationFrame().
    //
    // Sidenote: this is a clear violation of JS1K rules, which is why it's very important not to read them
    // before the competition is over.

    // A(top)

    // Now, since our demo is fairly heavy we use a small canvas, but we want it to be fullscreen on a
    // black background, so we waste ~90 bytes on some CSS to stretch it (currently "object-fit:contain"
    // doesn't work for canvas on MS browsers).
    //
    // To avoid wasting 90 bytes just on this, we take this opportunity to define P and Q as 'width' and
    // 'height' for later. This is probably a mistake since I ended up packing it with regpack anyway.
    //
    // The weird bit at the end is an ES6 template literal being abused to call the array's join method
    // with something that will be coerced into the string ':100%;'.

    // a.style=[P='width',Q='height','object-fit:contain;background:#000'].join`:100%;`

    canvas.style = 'width: 100%; height: 100%; object-fit:contain; background:#000;'

    // Now we need a frame counter.

    // t=0

    let frame = 0

    // B() is the requestAnimationFrame callback.

    // B=_=>{

    function onFrame() {
        // Set width and height on our canvases, we'll be using a smaller canvas for the godrays. This
        // also clears and resets their states. While we're at it, we'll store their dimensions in one
        // letter vars for later.

        // w=a[P]=512
        // h=a[Q]=256
        // W=E[P]=128
        // H=E[Q]=64

        canvas.width = 512
        canvas.height = 256
        godraysCanvas.width = 128
        godraysCanvas.height = 64

        // Set the sun's vertical position.

        // T=C(t++/w)*24

        let sunY = Math.cos(frame++ / 512) * 24 // This is actually the offset from the middle of the canvas.

        // Get the 2D context for our godrays canvas, and create abbreviations for all the context properties.

        // A(F=E.getContext`2d`)

        let godraysCtx = godraysCanvas.getContext('2d')

        // Now we set the godrays' context fillstyle (window.fy is 'fillStyle') to a newly created gradient
        // (cr is 'createRadialGradient') which we also run through our abbreviator.

        // A(F[fy]=g=F[cR](H,32+T,0,H,32+T,44)) // Could have shaved one more char here...

        let emissionGradient = godraysCtx.createRadialGradient(
            godraysCanvas.width / 2, godraysCanvas.height / 2 + sunY, // The sun's center.
            0,                                                        // Start radius.
            godraysCanvas.width / 2, godraysCanvas.height / 2 + sunY, // Sun's center again.
            44                                                        // End radius.
        )
        godraysCtx.fillStyle = emissionGradient

        // Now we addColorStops. This needs to be a dark gradient because our godrays effect will basically
        // overlay it on top of itself many many times, so anything lighter will result in lots of white.
        //
        // If you're not space-bound you can add another stop or two, maybe fade out to black, but this
        // actually looks good enough.

        // g[ao](.1,'#0C0804')
        // g[ao](.2,'#060201')

        emissionGradient.addColorStop(.1, '#0C0804') // Color for pixels in radius 0 to 4.4 (44 * .1).
        emissionGradient.addColorStop(.2, '#060201') // Color for everything past radius 8.8.

        // Now paint the gradient all over our godrays canvas.

        // F[fc](0,0,W,H)

        godraysCtx.fillRect(0, 0, godraysCanvas.width, godraysCanvas.height)

        // And set the fillstyle to black, we'll use it to paint our occlusion (mountains).

        // F[fy]='#000'

        godraysCtx.fillStyle = '#000'

        // For our 1K demo, we paint our sky a solid #644 reddish-brown. But here - let's do it right.

        // c[fy]=g='#644'
        // c[fc](0,0,w,h)

        let skyGradient = ctx.createLinearGradient(0, 0, 0, canvas.height)
        skyGradient.addColorStop(0, '#2a3e55') // Blueish at the top.
        skyGradient.addColorStop(.7, '#8d4835') // Reddish at the bottom.
        ctx.fillStyle = skyGradient
        ctx.fillRect(0, 0, canvas.width, canvas.height)

        // Our mountains will be made by summing up sine waves of varying frequencies and amplitudes.

        // m=(f,j)=>[1721,947,547,233,73,31,7].reduce((a,v)=>a*j-C(f*v),0)

        function mountainHeight(position, roughness) {
            // Our frequencies (prime numbers to avoid extra repetitions).
            let frequencies = [1721, 947, 547, 233, 73, 31, 7]
            // Add them up.
            return frequencies.reduce((height, freq) => height * roughness - Math.cos(freq * position), 0)
        }

        // Draw 4 layers of mountains.

        // for(i=0;i<4;i++)for(X=w,c[fy]=`hsl(7,23%,${23-i*6}%`;X--;F[fc](X/4,U/4+1,1,w))c[fc](X,U=W+i*25+m((t+t*i*i)/1e3+X/2e3,i/19-.5)*45,1,w)

        for (let i = 0; i < 4; i++) {
            // Set the main canvas fillStyle to a shade of brown with variable lightness (darker at the front).
            ctx.fillStyle = `hsl(7, 23%, ${23 - i * 6}%)`
            // For each column in our canvas...
            for (let x = canvas.width; x--;) {
                // Ok, I don't really remember the details here, basically the (frame+frame*i*i) makes the
                // near mountains move faster than the far ones. We divide by large numbers because our
                // mountains repeat at position 1/7*Math.PI*2 or something like that...
                let mountainPosition = (frame + frame * i * i) / 1000 + x / 2000
                // Make further mountains more jagged, adds a bit of realism and also makes the godrays
                // look nicer.
                let mountainRoughness = i / 19 - .5
                // 128 is the middle, i * 25 moves the nearer mountains lower on the screen.
                let y = 128 + i * 25 + mountainHeight(mountainPosition, mountainRoughness) * 45
                // Paint a 1px-wide rectangle from the mountain's top to below the bottom of the canvas.
                ctx.fillRect(x, y, 1, 999) // 999 can be any large number...
                // Paint the same thing in black on the godrays emission canvas, which is 1/4 the size,
                // and move it one pixel down (otherwise there can be a tiny underlit space between the
                // mountains and the sky).
                godraysCtx.fillRect(x / 4, y / 4 + 1, 1, 999)
            }
        }

        // The godrays are generated by adding up RGB values, gCt is the bane of all js golfers -
        // globalCompositeOperation. Set it to 'lighter' on both canvases.

        // c[gCt]=F[gCt]='lighter'

        ctx.globalCompositeOperation = godraysCtx.globalCompositeOperation = 'lighter'

        // NOW - let's light this motherfucker up! We'll make several passes over our emission canvas,
        // each time adding an enlarged copy of it to itself so at the first pass we get 2 copies, then 4,
        // then 8, then 16 etc... We square our scale factor at each iteration.

        // for(s=1.07;s<5;s*=s)F[da](E,(W-W*s)/2,(H-H*s)/2-T*s+T,W*s,H*s)

        for (let scaleFactor = 1.07; scaleFactor < 5; scaleFactor *= scaleFactor) {
            // The x, y, width and height arguments for drawImage keep the light source (godraysCanvas.width
            // / 2, godraysCanvas.height / 2 + sunY) in the same spot on the enlarged copy. It basically boils
            // down to multiplying a 2D matrix by itself. There's probably a better way to do this, but I
            // couldn't figure it out.
            godraysCtx.drawImage(
                godraysCanvas,
                (godraysCanvas.width - godraysCanvas.width * scaleFactor) / 2,
                (godraysCanvas.height - godraysCanvas.height * scaleFactor) / 2 - sunY * scaleFactor + sunY,
                godraysCanvas.width * scaleFactor,
                godraysCanvas.height * scaleFactor
            )
        }

        // Now that our godrays are rendered, draw them to our output canvas (whose globalCompositeOperation
        // is already set to 'lighter').

        // c[da](E,0,0,w,h)

        ctx.drawImage(godraysCanvas, 0, 0, canvas.width, canvas.height)

        // All done.

        // this[rte](B)}

        window.requestAnimationFrame(onFrame)
    }

    // Call our requestAnimationFrame handler to start rendering. Since it takes no arguments use the argument
    // list to create our godrays canvas with cloneNode, which also takes no arguments... use it to setup a
    // Math.cos shortcut (we'll skip this in our longform version).

    // B(E=a.cloneNode(C=Math.cos))

    let godraysCanvas = canvas.cloneNode()
    onFrame()

    // Phew... that took a while, but we're finally done with the visuals. Now for the audio part -
    //
    // The synthesizer is based on the Karplus-Strong algorithm which uses a very short delay loop as a
    // resonator. I was initially aiming for a realistic string quartet but time and space constraints
    // have forced me to massively compromise.
    //
    //
    // The music is a 64-note melody that ends up an octave above where it started, spread out in a 4-voice
    // canon. We pre-render a single voice and then add up 4 in our ScriptProcessor callback.

    // Big hairy render loop, let's break it to pieces and explain...

    // for(M=[Y=[V=J=I=i=0]];i<h;i++)for(j=2e4;j--;T=Y[I|0]=M[J++]=O%9)O=Math.random()-.5+T/5+Y[(I=++I%(7e3/2**(("!!----,*,(444420/20/-0/---,,--//((4444202/;;;;986986420/00--//,,".charCodeAt(i&63)+12*(i>>6))/12)))|0]*.8||0

    let encodedMelody = "!!----,*,(444420/20/-0/---,,--//((4444202/;;;;986986420/00--//,,"

    // M=[Y=[V=J=I=i=0]]
    let voiceBuffer = [] // M = [...]
    let ksDelayBuffer = [] // Y = [...]
    let sampleOffset = 0 // V = 0 (used later)
    let J = 0 // What the hell is J????

    // Oh fuck it. It's 4am and I have no idea how this thing works. Maybe I'll write it up later.
    // Besides, you just came here for the godrays, right?

    // A(G=new AudioContext)
    // A(S=G[cSr](w*8,0,1))
    // S[oo]=e=>{A(e);A(o=e[oB]);for(i=0;i<w*8;o[gn](0)[i++]=O/32,V++)for(O=0,K=4;K--;O+=T>0&&M[T%J])T=V-(K/32*9)*J}
    // S.connect(G[da])
}
