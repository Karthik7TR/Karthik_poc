function Spinner(parent) {
    this.div = createSpinnerContainer(parent);
    this.spinner = createSpinner(this.div);
    this.parent = parent;

    this.spin = function () {
        let self = this;
        this.spinner.addEventListener('transitionend', function () {
            self.spinner.classList.add('sap-spinner-spinning');
        });
        this.div.classList.remove('transparent');
        this.div.classList.add('transparent-white');
        this.spinner.classList.remove('sap-spinner-hidden');
        this.spinner.classList.add('sap-spinner-appeared');
    }

    this.stop = function () {
        let self = this;
        this.spinner.addEventListener('animationiteration', function () {
            self.spinner.addEventListener('transitionend', function () {
                self.parent.removeChild(self.div);
            });
            self.spinner.classList.remove('sap-spinner-spinning');
            self.spinner.classList.remove('sap-spinner-appeared');
            self.spinner.classList.add('sap-spinner-hidden');
            self.div.classList.remove('transparent-white');
            self.div.classList.add('transparent');
        });
    }

    function createSpinnerContainer(parent) {
        let div = document.createElement('div');
        div.id = 'sap-spinner-container';
        div.classList.add('transparent');
        parent.insertBefore(div, parent.firstChild);
        return div;
    }

    function createSpinner(spinnerContainer) {
        let spinner = document.createElement('img');
        spinner.id = 'sap-spinner';
        spinner.src = 'theme/images/sap/spinner.png';
        spinner.classList.add('sap-spinner-hidden');
        spinnerContainer.appendChild(spinner);
        return spinner;
    }
}