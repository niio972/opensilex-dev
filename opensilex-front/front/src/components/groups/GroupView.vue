<template>
  <div>
    <b-button @click="showCreateForm" variant="success">Add group</b-button>
    <opensilex-GroupForm
      ref="groupForm"
      @onCreate="callCreateGroupService"
      @onUpdate="callUpdateGroupService"
      :profiles="profiles"
    ></opensilex-GroupForm>
    <opensilex-GroupList ref="groupList" @onEdit="editGroup" @onDelete="deleteGroup"></opensilex-GroupList>
  </div>
</template>

<script lang="ts">
import { Component } from "vue-property-decorator";
import Vue from "vue";
import HttpResponse, { OpenSilexResponse } from "../../lib/HttpResponse";
import {
  GroupService,
  GroupCreationDTO,
  GroupUpdateDTO,
  GroupGetDTO,
  ProfileService,
  ProfileGetDTO
} from "opensilex-rest/index";

@Component
export default class GroupView extends Vue {
  $opensilex: any;
  $store: any;
  service: GroupService;
  profiles: Array<ProfileGetDTO> = [];

  get user() {
    return this.$store.state.user;
  }

  async created() {
    this.service = this.$opensilex.getService("opensilex.GroupService");
    console.debug("Loading profiles list...");
    let profileService: ProfileService = await this.$opensilex.loadService(
      "opensilex-rest.ProfileService"
    );
    let http: HttpResponse<OpenSilexResponse<
      Array<ProfileGetDTO>
    >> = await profileService.getAllProfiles(
      this.$opensilex.getUser().getAuthorizationHeader()
    );
    this.profiles = http.response.result;
    console.debug("Profiles list loaded !", this.profiles);
  }

  showCreateForm() {
    let groupForm: any = this.$refs.groupForm;
    groupForm.showCreateForm();
  }

  callCreateGroupService(form: GroupCreationDTO, done) {
    done(
      this.service
        .createGroup(this.user.getAuthorizationHeader(), form)
        .then((http: HttpResponse<OpenSilexResponse<any>>) => {
          let uri = http.response.result;
          console.debug("Group created", uri);
          let groupList: any = this.$refs.groupList;
          groupList.refresh();
        })
    );
  }

  callUpdateGroupService(form: GroupUpdateDTO, done) {
    done(
      this.service
        .updateGroup(this.user.getAuthorizationHeader(), form)
        .then((http: HttpResponse<OpenSilexResponse<any>>) => {
          let uri = http.response.result;
          console.debug("Group updated", uri);
          let groupList: any = this.$refs.groupList;
          groupList.refresh();
        })
    );
  }

  editGroup(form: GroupGetDTO) {
    let groupForm: any = this.$refs.groupForm;
    groupForm.showEditForm(form);
  }

  deleteGroup(uri: string) {
    this.service
      .deleteGroup(this.user.getAuthorizationHeader(), uri)
      .then(() => {
        let groupList: any = this.$refs.groupList;
        groupList.refresh();
      })
      .catch(this.$opensilex.errorHandler);
  }
}
</script>

<style scoped lang="scss">
</style>

