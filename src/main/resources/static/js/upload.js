// constants
const actionUrl = document.getElementById("uploadForm").getAttribute('action');

// Adds the FileName to the corresponding Label Element
function setFileLabelText() {
    var labelElement = this.parentElement.lastElementChild

    labelElement.innerHTML = this.value.split("\\").pop();
    labelElement.style.color = "black";
}

// Helper function for an alert 
function showAlertFileSize(message, event) {
    if (event.lengthComputable) {
        alert(`${message}: Loaded ${event.loaded} of ${event.total} bytes`);
    } else {
        alert(`${message}:Loaded ${event.loaded} bytes`);
    }
}

// uploads the File
function uploadFile(e) {
    e.preventDefault();

    const xhr = new XMLHttpRequest();
    var totalFileSize;
    var loadedFileSize;

    var labelElement = this.querySelector("div").querySelector("label");
    var progressBar = this.querySelector(".progress").querySelector(".progress-bar");


    if (labelElement.innerHTML != "Choose File" && labelElement.innerHTML != "") {
        progressBar.parentElement.classList.remove("invisible");


        xhr.open('POST', actionUrl);
        xhr.upload.addEventListener("progress", e => {
            const percent = e.lengthComputable ? (e.loaded / e.total) * 100 : 0;
            totalFileSize = e.total;
            loadedFileSize = e.loaded;
            progressBar.style.width = percent.toFixed(2) + "%";
            progressBar.innerHTML = percent.toFixed(2) + "%";
            //console.log(e);
        });

        xhr.upload.addEventListener("loadend", e => {
            console.log(e);
            //showAlertFileSize("End",e);
        });

        xhr.addEventListener("error", e => {
            showAlertFileSize("Error callback", e);
            console.log(`totalfilesize: ${e.total}, loaded: ${e.loaded}`);
            console.log(`loaded ${loadedFileSize} of ${totalFileSize}`);

        });


        xhr.addEventListener("load", event => {
            var actionList = actionUrl.replace('addfile', 'edit');
            console.log(`loaded ${loadedFileSize} of ${totalFileSize}`);
            window.location.href = actionList;
        });


        //xhr.setRequestHeader("Content-Type", "multipart/form-data");
        xhr.send(new FormData(this));
    } else {
        labelElement.style.color = "red";
    }
}

// Add EventListeners
document.querySelectorAll(".custom-file-input").forEach(item => {
  // Add Change Listener to all InputElements
  item.addEventListener("change", setFileLabelText);
  // Add Submit Listener to all Forms
  item.parentElement.parentElement.addEventListener("submit", uploadFile);

});

